package dev.cypherfury.juniscan.service;

import dev.cypherfury.juniscan.entity.Extrinsic;
import dev.cypherfury.juniscan.entity.Function;
import dev.cypherfury.juniscan.entity.Module;
import dev.cypherfury.juniscan.utils.ByteUtils;
import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing `Extrinsic` entities.
 * <p>
 * Responsibilities:
 * - Decodes extrinsic data from raw byte arrays.
 * - Builds `Extrinsic` entities with associated `Module` and `Function`.
 * - Provides methods to decode single or multiple extrinsics.
 *
 * @author Cypher
 */
@Slf4j
@Service
public class ExtrinsicService {

    private static final byte UNSIGNED_TX_VALUE = 4;

    private final FunctionService functionService;
    private final ModuleService moduleService;

    /**
     * Constructor for `ExtrinsicService`.
     *
     * @param functionService service for retrieving `Function` entities.
     * @param moduleService   service for retrieving `Module` entities.
     */
    public ExtrinsicService(FunctionService functionService, ModuleService moduleService) {
        this.functionService = functionService;
        this.moduleService = moduleService;
    }

    /**
     * Decodes a list of extrinsics from their string representations.
     *
     * @param extrinsics list of string representations of extrinsics.
     * @return a list of decoded `Extrinsic` entities.
     */
    public List<Extrinsic> decode(List<String> extrinsics) {
        return extrinsics.stream()
                .map(ByteUtils::getBytes)
                .map(this::decodeBytes)
                .toList();
    }

    /**
     * Decodes a single extrinsic from its raw byte representation.
     *
     * @param extrinsicBytes raw byte array representing the extrinsic.
     * @return the decoded `Extrinsic` entity.
     */
    private Extrinsic decodeBytes(byte[] extrinsicBytes) {
        ScaleCodecReader reader = new ScaleCodecReader(extrinsicBytes);
        int size = reader.readByte();
        boolean signed = reader.readByte() != UNSIGNED_TX_VALUE;
        if (!signed) {
            Module module = moduleService.getById(reader.readByte());
            Function function = functionService.getByModuleAndId(module, reader.readByte());
            return build(size, signed, module, function);
        }
        return null;
    }

    /**
     * Builds an `Extrinsic` entity with the given properties.
     *
     * @param size   the size of the extrinsic.
     * @param signed  the type of the extrinsic.
     * @param module   the associated `Module`.
     * @param function the associated `Function`.
     * @return the constructed `Extrinsic` entity.
     */
    private Extrinsic build(int size, boolean signed, Module module, Function function) {
        return Extrinsic.builder()
                .function(function)
                .signed(signed)
                .module(module)
                .size(size)
                .build();
    }

}
