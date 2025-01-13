-- Table for Block
CREATE TABLE block
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY, -- Auto-incremented primary key
    parent_hash     VARCHAR(255),                      -- Parent hash of the block
    number          VARCHAR(255),                      -- Number of the block
    state_root      VARCHAR(255),                      -- State root of the block
    extrinsics_root VARCHAR(255)                       -- Extrinsics root of the block
);

-- Table for logs as an element collection in Block
CREATE TABLE block_logs
(
    block_id BIGINT       NOT NULL,              -- Foreign key referencing the Block table
    logs     VARCHAR(255) NOT NULL,              -- Individual log entry
    PRIMARY KEY (block_id, logs),                -- Composite primary key to ensure uniqueness
    FOREIGN KEY (block_id) REFERENCES block (id) -- Foreign key constraint linking to Block
);

-- Table for Module
CREATE TABLE module
(
    id          BIGINT PRIMARY KEY,           -- Primary key for the module table (no auto-increment)
    name        VARCHAR(255) NOT NULL UNIQUE, -- Unique name of the module
    description VARCHAR(255) NOT NULL         -- Description of the module
);

-- Table for Function
CREATE TABLE function
(
    id          BIGINT PRIMARY KEY,                -- Auto-incremented primary key for each function
    call_index  INT           NOT NULL,            -- Index of the function within the module
    name        VARCHAR(255)  NOT NULL,            -- Name of the function
    description VARCHAR(5000) NOT NULL,            -- Description of the function
    module_id   BIGINT        NOT NULL,            -- Foreign key referencing the module table
    FOREIGN KEY (module_id) REFERENCES module (id) -- Foreign key constraint linking to the module table
);

-- Table for Extrinsic
CREATE TABLE extrinsic
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Auto-incremented primary key for each extrinsic
    signed         BOOLEAN NOT NULL,                   -- Indicates if the extrinsic is signed
    size           BIGINT  NOT NULL,                   -- Size of the extrinsic in bytes
    version        BIGINT  NOT NULL,                   -- Version of the extrinsic in bytes
    address_prefix VARCHAR(255),                       -- Address prefix of the signer
    issuer_address VARCHAR(255),                       -- Address of the issuer
    signature_type VARCHAR(255),                       -- Type of signature used (Ed25519, Sr25519, ECDSA)
    signature      TEXT,                               -- Signature of the extrinsic
    era_period     INT,                                -- Era period of the extrinsic
    era_phase      INT,                                -- Era phase of the extrinsic
    nonce          BIGINT,                             -- Nonce of the extrinsic
    tip            BIGINT,                             -- Tip (fee) offered for the extrinsic
    block_id       BIGINT,                             -- Foreign key referencing the block table
    module_id      BIGINT,                             -- Foreign key referencing the module table
    function_id    BIGINT,                             -- Foreign key referencing the function table
    FOREIGN KEY (block_id) REFERENCES block (id),      -- Foreign key constraint linking to the block table
    FOREIGN KEY (module_id) REFERENCES module (id),    -- Foreign key constraint linking to the module table
    FOREIGN KEY (function_id) REFERENCES function (id) -- Foreign key constraint linking to the function table
);

-- Create table for FunctionParameter
CREATE TABLE function_parameter
(
    id          BIGINT PRIMARY KEY,                    -- Primary key with auto-increment
    name        VARCHAR(255)  NOT NULL,                -- Name of the parameter (cannot be null)
    type        VARCHAR(1000) NOT NULL,                -- Data type of the parameter (cannot be null)
    function_id BIGINT        NOT NULL,                -- Foreign key referencing the function table
    FOREIGN KEY (function_id) REFERENCES function (id) -- Foreign key constraint linking to the function table
);