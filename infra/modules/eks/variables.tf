variable "project" {
  type = string
}

variable "subnet_ids" {
  type = list(string)
}

variable "eks_sa_policies" {
  type = list(string)
}

variable "eks_sa_name" {
  type = string
}