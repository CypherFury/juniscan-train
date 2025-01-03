package dev.cypherfury.juniscan.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the details of a blockchain block.
 * <p>
 * This DTO is used to deserialize responses containing detailed information about a block,
 * including its header, extrinsics, and justifications.
 * <p>
 * The structure includes nested static classes to represent hierarchical data in the block.
 * <p>
 * Dependencies:
 * - Uses Lombok {@link Data} annotation to auto-generate getters, setters, and other methods.
 *
 * @author Cypherfury
 */
@Data
public class BlockDetailsDTO {

    private Block block;
    private Object justifications;

    /**
     * Represents a blockchain block, including its header and extrinsics.
     */
    @Data
    public static class Block {

        private Header header;
        private String[] extrinsics;

        /**
         * Represents the header of a blockchain block.
         */
        @Data
        public static class Header {

            private String parentHash;
            private String number;
            private String stateRoot;
            private String extrinsicsRoot;
            private Digest digest;

            /**
             * Represents the digest of a blockchain block.
             */
            @Data
            public static class Digest {
                private String[] logs;
            }

        }
    }
}
