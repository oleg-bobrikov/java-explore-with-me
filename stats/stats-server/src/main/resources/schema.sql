CREATE TABLE IF NOT EXISTS public.endpoint_hits
(
    id          BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    app         varchar(255) NOT NULL,
    ip          varchar(255) NOT NULL,
    "timestamp" timestamp    NOT NULL,
    uri         varchar(255) NOT NULL,
    PRIMARY KEY (id)
);