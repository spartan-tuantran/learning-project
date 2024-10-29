terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.43"
    }

    tls = {
      source  = "hashicorp/tls"
      version = "4.0.5"
    }

    null = {
      source  = "hashicorp/null"
      version = "3.2.2"
    }

    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.31.0"
    }
  }
}

provider "aws" {
  region = local.aws_region
}