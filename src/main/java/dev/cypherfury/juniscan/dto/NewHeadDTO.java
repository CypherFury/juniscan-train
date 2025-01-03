package dev.cypherfury.juniscan.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing a new block head notification in the blockchain.
 * <p>
 * This DTO is used to deserialize JSON-RPC notifications for new block headers,
 * typically received from a WebSocket connection to a blockchain node.
 * <p>
 * The structure includes nested static classes to represent hierarchical data for
 * the notification parameters, block details, and digest logs.
 * <p>
 * Dependencies:
 * - Uses Lombok {@link Data} annotation to auto-generate getters, setters, and other methods.
 *
 * @author Cypherfury
 */
@Data
public class NewHeadDTO {

    private String jsonrpc;
    private String method;
    private Params params;

    /**
     * Represents the parameters of the notification, including subscription information
     * and the result containing block details.
     */
    @Data
    public static class Params {

        private String subscription;
        private Result result;

        /**
         * Represents the result of the notification, which includes block metadata
         * such as parent hash, block number, state root, and extrinsics root.
         */
        @Data
        public static class Result {

            private String parentHash;
            private String number;
            private String stateRoot;
            private String extrinsicsRoot;
            private Digest digest;

            /**
             * Represents the digest of the block, including logs for consensus metadata or other auxiliary information.
             */
            @Data
            public static class Digest {
                private String[] logs;
            }

        }
    }
}
