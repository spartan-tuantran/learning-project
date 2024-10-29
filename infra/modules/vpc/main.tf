# Fetch all availability zones in region
data "aws_availability_zones" "azs" {
  state = "available"
}

locals {
  azs       = data.aws_availability_zones.azs.names
  azs_count = length(local.azs)
}

# Create a VPC
resource "aws_vpc" "vpc" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "${var.project}-vpc"
  }
}

# Create public subnets across availability zones
resource "aws_subnet" "public_subnet" {
  count                   = length(var.public_cidrs)
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = var.public_cidrs[count.index]
  availability_zone       = local.azs[count.index % local.azs_count]
  map_public_ip_on_launch = true

  tags = {
    Name = format(
      "%s-public-subnet-%d-%s",
      var.project,
      count.index,
      local.azs[count.index % local.azs_count]
    )
  }
}

# Create private subnets across availability zones
resource "aws_subnet" "private_subnet" {
  count                   = length(var.private_cidrs)
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = var.private_cidrs[count.index]
  availability_zone       = local.azs[count.index % local.azs_count]
  map_public_ip_on_launch = false

  tags = {
    Name = format(
      "%s-private-subnet-%d-%s",
      var.project,
      count.index,
      local.azs[count.index % local.azs_count]
    )
  }
}

# Create an Internet Gateway for public subnets
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = "${var.project}-internet-gateway"
  }
}

# Create a public route table
resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "${var.project}-public-rt"
  }
}

# Associate public subnets with the public route table
resource "aws_route_table_association" "public_subnet_asso" {
  count          = length(aws_subnet.public_subnet)
  route_table_id = aws_route_table.public_rt.id
  subnet_id      = aws_subnet.public_subnet[count.index].id
}

# Create an Elastic IP for the NAT Gateway
resource "aws_eip" "nat_eip" {
  domain = "vpc"
}

# Create a NAT Gateway for private subnets
resource "aws_nat_gateway" "nat_gw" {
  subnet_id     = aws_subnet.public_subnet[0].id
  allocation_id = aws_eip.nat_eip.id

  tags = {
    Name = "${var.project}-nat-gw"
  }
}

# Create a private route table
resource "aws_route_table" "private_rt" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat_gw.id
  }

  tags = {
    Name = "${var.project}-private-rt"
  }
}

# Associate private subnets with the private route table
resource "aws_route_table_association" "private_route_table_asso" {
  count          = length(aws_subnet.private_subnet)
  route_table_id = aws_route_table.private_rt.id
  subnet_id      = aws_subnet.private_subnet[count.index].id
}
