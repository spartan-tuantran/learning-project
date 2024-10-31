-- 1. Create the table
CREATE TABLE public.refresh_token
(
  id         UUID                     NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id    UUID                     NOT NULL,
  token      TEXT                     NOT NULL,
  expires_in BIGINT                   NOT NULL DEFAULT 604800,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL             DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE,
  deleted_at TIMESTAMP WITH TIME ZONE
);

-- 2. Create the index
CREATE UNIQUE INDEX index_refresh_token_unique_token ON public.refresh_token (token) WHERE deleted_at IS NULL;

-- 3. Create on update procedure
CREATE TRIGGER trigger_users_updated_at
  BEFORE UPDATE
  ON public.refresh_token
  FOR EACH ROW
  EXECUTE PROCEDURE trigger_updated_at();
