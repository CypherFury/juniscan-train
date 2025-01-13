package dev.cypherfury.juniscan.entity.enums;

public enum SignatureType {

    ED25519("Ed25519"),
    SR25519("Sr25519"),
    ECDSA("ECDSA");

    private final String typeName;

    SignatureType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static SignatureType fromByte(byte signatureTypeByte) {
        return switch (signatureTypeByte) {
            case 0x00 -> ED25519;
            case 0x01 -> SR25519;
            case 0x02 -> ECDSA;
            default -> throw new IllegalArgumentException("Type de signature inconnu : " + signatureTypeByte);
        };
    }

}
