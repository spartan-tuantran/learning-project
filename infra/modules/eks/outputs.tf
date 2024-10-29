output "cluster_info" {
  value = {
    id                         = aws_eks_cluster.cluster.id
    cluster_api_endpoint       = aws_eks_cluster.cluster.endpoint
    certificate_authority_data = aws_eks_cluster.cluster.certificate_authority[0].data
    oidc_provider_url          = aws_iam_openid_connect_provider.eks_oidc.url
  }
}