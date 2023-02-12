CREATE TABLE transaction1
(
    id                                  UUID PRIMARY KEY,
    amount                              bigint,
    unreconciled_amount                 bigint,
    status                              varchar
);

CREATE TABLE transaction2
(
    id                                  UUID PRIMARY KEY,
    amount                              bigint,
    unreconciled_amount                 bigint,
    status                              varchar
);

CREATE TABLE matching_lock
(
    id                                  UUID PRIMARY KEY,
    transaction1_id                     UUID UNIQUE NOT NULL,
    transaction2_id                     UUID UNIQUE NOT NULL
);

CREATE TABLE matching_result
(
    id                                  UUID PRIMARY KEY,
    transaction1_id                     UUID CONSTRAINT fk_mr_transaction1 REFERENCES transaction1,
    transaction2_id                     UUID CONSTRAINT fk_mr_transaction2 REFERENCES transaction2
);
