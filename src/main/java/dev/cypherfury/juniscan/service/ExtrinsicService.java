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

    private static final int MSB_MASK = 0x80;

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
     * Decodes a single extrinsic from its raw byte representation.
     *
     * @param extrinsicBytes raw byte array representing the extrinsic.
     * @return the decoded `Extrinsic` entity.
     */
    public Extrinsic decode(byte[] extrinsicBytes) {
        ScaleCodecReader reader = new ScaleCodecReader(extrinsicBytes);
        int header = reader.readByte();
        int version = reader.readByte();
        Module module = moduleService.getById(reader.readByte());
        Function function = functionService.getByModuleAndId(module, reader.readByte());
        return build(header, version, module, function);
    }

    /**
     * Builds an `Extrinsic` entity with the given properties.
     *
     * @param header   the header of the extrinsic.
     * @param version  the version of the extrinsic.
     * @param module   the associated `Module`.
     * @param function the associated `Function`.
     * @return the constructed `Extrinsic` entity.
     */
    private Extrinsic build(int header, int version, Module module, Function function) {
        return Extrinsic.builder()
                .signed(isSigned(header))
                .function(function)
                .version(version)
                .header(header)
                .module(module)
                .build();
    }

    /**
     * Determines whether an extrinsic is signed based on its header.
     *
     * @param header the header byte of the extrinsic.
     * @return `true` if the extrinsic is signed, `false` otherwise.
     */
    private boolean isSigned(int header) {
        return (header & MSB_MASK) != 0;
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
                .map(this::decode)
                .toList();
    }

}
