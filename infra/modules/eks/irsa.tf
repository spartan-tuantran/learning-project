data "tls_certificate" "oidc_certificate" {
  url = aws_eks_cluster.cluster.identity[0].oidc[0].issuer
}

resource "aws_iam_openid_connect_provider" "eks_oidc" {
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = [data.tls_certificate.oidc_certificate.certificates[0].sha1_fingerprint]
  url             = aws_eks_cluster.cluster.identity[0].oidc[0].issuer
  depends_on      = [aws_eks_cluster.cluster]
}

data "aws_iam_policy_document" "assume_role_service_account" {
  statement {
    actions = ["sts:AssumeRoleWithWebIdentity"]
    effect  = "Allow"
    principals {
      identifiers = [aws_iam_openid_connect_provider.eks_oidc.arn]
      type        = "Federated"
    }
    condition {
      test     = "StringEquals"
      values   = ["sts.amazonaws.com"]
      variable = "${aws_iam_openid_connect_provider.eks_oidc.url}:aud"
    }
    condition {
      test     = "StringEquals"
      values   = ["system:serviceaccount:default:${var.eks_sa_name}"]
      variable = "${aws_iam_openid_connect_provider.eks_oidc.url}:sub"
    }
  }
}

resource "aws_iam_role" "eks_irsa_role" {
  name               = "eks-irsa-role"
  assume_role_policy = data.aws_iam_policy_document.assume_role_service_account.json
  depends_on         = [aws_iam_openid_connect_provider.eks_oidc]
}

data "aws_iam_policy_document" "irsa_policy_document" {
  statement {
    actions = var.eks_sa_policies
    effect  = "Allow"
    resources = ["*"]
  }
}

resource "aws_iam_role_policy" "default" {
  name   = "eks-irsa-policy"
  role   = aws_iam_role.eks_irsa_role.id
  policy = data.aws_iam_policy_document.irsa_policy_document.json
}