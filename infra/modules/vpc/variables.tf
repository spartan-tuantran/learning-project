variable "vpc_cidr" {
  type        = string
}

variable "project" {
  type        = string
}

variable "private_cidrs" {
  type        = list(string)
}

variable "public_cidrs" {
  type        = list(string)
}