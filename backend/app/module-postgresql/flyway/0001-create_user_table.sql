-- 1. Create the table
CREATE TABLE public.user
(
  id            UUID                     NOT NULL PRIMARY KEY DEFAULT uuid_generate_v4(),
  username      TEXT UNIQUE              NOT NULL,
  password      TEXT,
  role          TEXT                     NOT NULL             DEFAULT 'USER',
  last_login_at TIMESTAMP WITH TIME ZONE,
  created_at    TIMESTAMP WITH TIME ZONE NOT NULL             DEFAULT NOW(),
  updated_at    TIMESTAMP WITH TIME ZONE,
  deleted_at    TIMESTAMP WITH TIME ZONE
);

-- 2. Create the index
CREATE UNIQUE INDEX index_user_unique_username ON public.user (username) WHERE deleted_at IS NULL;

-- 3. Create on update procedure
CREATE TRIGGER trigger_users_updated_at
  BEFORE UPDATE
  ON public.user
  FOR EACH ROW
  EXECUTE PROCEDURE trigger_updated_at();
