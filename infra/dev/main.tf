module "vpc" {
  source = "../modules/vpc"

  project       = local.project
  vpc_cidr      = "10.0.0.0/16"
  public_cidrs  = ["10.0.1.0/24", "10.0.2.0/24"]
  private_cidrs = ["10.0.5.0/24", "10.0.6.0/24"]
}

module "eks" {
  source = "../modules/eks"

  project    = local.project
  subnet_ids = module.vpc.private_subnet_ids
  eks_sa_policies = [
    "s3:ListBucket",
    "s3:GetObject"
  ]
  eks_sa_name = "my_service_account"
}

output "cluster_info" {
  value = module.eks.cluster_info
}