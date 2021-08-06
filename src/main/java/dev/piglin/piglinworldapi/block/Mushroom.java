package dev.piglin.piglinworldapi.block;

import org.bukkit.Material;

/**
 * Mushrooms' block states have 6 boolean fields, this enum lists all possible values
 */
@SuppressWarnings("unused")
public enum Mushroom {

    RED_001100(MushroomType.RED, (byte) 0b001100),
    RED_001101(MushroomType.RED, (byte) 0b001101),
    RED_001110(MushroomType.RED, (byte) 0b001110),
    RED_001111(MushroomType.RED, (byte) 0b001111),
    RED_010001(MushroomType.RED, (byte) 0b010001),
    RED_010011(MushroomType.RED, (byte) 0b010011),
    RED_010101(MushroomType.RED, (byte) 0b010101),
    RED_010111(MushroomType.RED, (byte) 0b010111),
    RED_011001(MushroomType.RED, (byte) 0b011001),
    RED_011011(MushroomType.RED, (byte) 0b011011),
    RED_011100(MushroomType.RED, (byte) 0b011100),
    RED_011101(MushroomType.RED, (byte) 0b011101),
    RED_011110(MushroomType.RED, (byte) 0b011110),
    RED_011111(MushroomType.RED, (byte) 0b011111),
    RED_100000(MushroomType.RED, (byte) 0b100000),
    RED_100001(MushroomType.RED, (byte) 0b100001),
    RED_100010(MushroomType.RED, (byte) 0b100010),
    RED_100011(MushroomType.RED, (byte) 0b100011),
    RED_100100(MushroomType.RED, (byte) 0b100100),
    RED_100101(MushroomType.RED, (byte) 0b100101),
    RED_100110(MushroomType.RED, (byte) 0b100110),
    RED_100111(MushroomType.RED, (byte) 0b100111),
    RED_101000(MushroomType.RED, (byte) 0b101000),
    RED_101001(MushroomType.RED, (byte) 0b101001),
    RED_101010(MushroomType.RED, (byte) 0b101010),
    RED_101011(MushroomType.RED, (byte) 0b101011),
    RED_101100(MushroomType.RED, (byte) 0b101100),
    RED_101101(MushroomType.RED, (byte) 0b101101),
    RED_101110(MushroomType.RED, (byte) 0b101110),
    RED_101111(MushroomType.RED, (byte) 0b101111),
    RED_110000(MushroomType.RED, (byte) 0b110000),
    RED_110001(MushroomType.RED, (byte) 0b110001),
    RED_110010(MushroomType.RED, (byte) 0b110010),
    RED_110011(MushroomType.RED, (byte) 0b110011),
    RED_110100(MushroomType.RED, (byte) 0b110100),
    RED_110101(MushroomType.RED, (byte) 0b110101),
    RED_110110(MushroomType.RED, (byte) 0b110110),
    RED_110111(MushroomType.RED, (byte) 0b110111),
    RED_111000(MushroomType.RED, (byte) 0b111000),
    RED_111001(MushroomType.RED, (byte) 0b111001),
    RED_111010(MushroomType.RED, (byte) 0b111010),
    RED_111011(MushroomType.RED, (byte) 0b111011),
    RED_111100(MushroomType.RED, (byte) 0b111100),
    RED_111101(MushroomType.RED, (byte) 0b111101),
    RED_111110(MushroomType.RED, (byte) 0b111110),
    RED_111111(MushroomType.RED, (byte) 0b111111),

    /**
     * @deprecated cannot retexture item with CustomModelData=0 as it's the default value
     */
    @Deprecated
    RED_000000(MushroomType.RED, (byte) 0b000000),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_000010(MushroomType.RED, (byte) 0b000010),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_000111(MushroomType.RED, (byte) 0b000111),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_000101(MushroomType.RED, (byte) 0b000101),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_000001(MushroomType.RED, (byte) 0b000001),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_000011(MushroomType.RED, (byte) 0b000011),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_001011(MushroomType.RED, (byte) 0b001011),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_001001(MushroomType.RED, (byte) 0b001001),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_001010(MushroomType.RED, (byte) 0b001010),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_001000(MushroomType.RED, (byte) 0b001000),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_011000(MushroomType.RED, (byte) 0b011000),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_011010(MushroomType.RED, (byte) 0b011010),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_010010(MushroomType.RED, (byte) 0b010010),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_010000(MushroomType.RED, (byte) 0b010000),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_010100(MushroomType.RED, (byte) 0b010100),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_010110(MushroomType.RED, (byte) 0b010110),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_000100(MushroomType.RED, (byte) 0b000100),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    RED_000110(MushroomType.RED, (byte) 0b000110),

    BROWN_000001(MushroomType.BROWN, (byte) 0b000001),
    BROWN_000100(MushroomType.BROWN, (byte) 0b000100),
    BROWN_000101(MushroomType.BROWN, (byte) 0b000101),
    BROWN_000111(MushroomType.BROWN, (byte) 0b000111),
    BROWN_001000(MushroomType.BROWN, (byte) 0b001000),
    BROWN_001001(MushroomType.BROWN, (byte) 0b001001),
    BROWN_001100(MushroomType.BROWN, (byte) 0b001100),
    BROWN_001101(MushroomType.BROWN, (byte) 0b001101),
    BROWN_001110(MushroomType.BROWN, (byte) 0b001110),
    BROWN_001111(MushroomType.BROWN, (byte) 0b001111),
    BROWN_010000(MushroomType.BROWN, (byte) 0b010000),
    BROWN_010001(MushroomType.BROWN, (byte) 0b010001),
    BROWN_010011(MushroomType.BROWN, (byte) 0b010011),
    BROWN_010100(MushroomType.BROWN, (byte) 0b010100),
    BROWN_010101(MushroomType.BROWN, (byte) 0b010101),
    BROWN_010111(MushroomType.BROWN, (byte) 0b010111),
    BROWN_011000(MushroomType.BROWN, (byte) 0b011000),
    BROWN_011001(MushroomType.BROWN, (byte) 0b011001),
    BROWN_011011(MushroomType.BROWN, (byte) 0b011011),
    BROWN_011100(MushroomType.BROWN, (byte) 0b011100),
    BROWN_011101(MushroomType.BROWN, (byte) 0b011101),
    BROWN_011110(MushroomType.BROWN, (byte) 0b011110),
    BROWN_011111(MushroomType.BROWN, (byte) 0b011111),
    BROWN_100000(MushroomType.BROWN, (byte) 0b100000),
    BROWN_100001(MushroomType.BROWN, (byte) 0b100001),
    BROWN_100010(MushroomType.BROWN, (byte) 0b100010),
    BROWN_100011(MushroomType.BROWN, (byte) 0b100011),
    BROWN_100100(MushroomType.BROWN, (byte) 0b100100),
    BROWN_100101(MushroomType.BROWN, (byte) 0b100101),
    BROWN_100110(MushroomType.BROWN, (byte) 0b100110),
    BROWN_100111(MushroomType.BROWN, (byte) 0b100111),
    BROWN_101000(MushroomType.BROWN, (byte) 0b101000),
    BROWN_101001(MushroomType.BROWN, (byte) 0b101001),
    BROWN_101010(MushroomType.BROWN, (byte) 0b101010),
    BROWN_101011(MushroomType.BROWN, (byte) 0b101011),
    BROWN_101100(MushroomType.BROWN, (byte) 0b101100),
    BROWN_101101(MushroomType.BROWN, (byte) 0b101101),
    BROWN_101110(MushroomType.BROWN, (byte) 0b101110),
    BROWN_101111(MushroomType.BROWN, (byte) 0b101111),
    BROWN_110000(MushroomType.BROWN, (byte) 0b110000),
    BROWN_110001(MushroomType.BROWN, (byte) 0b110001),
    BROWN_110010(MushroomType.BROWN, (byte) 0b110010),
    BROWN_110011(MushroomType.BROWN, (byte) 0b110011),
    BROWN_110100(MushroomType.BROWN, (byte) 0b110100),
    BROWN_110101(MushroomType.BROWN, (byte) 0b110101),
    BROWN_110110(MushroomType.BROWN, (byte) 0b110110),
    BROWN_110111(MushroomType.BROWN, (byte) 0b110111),
    BROWN_111000(MushroomType.BROWN, (byte) 0b111000),
    BROWN_111001(MushroomType.BROWN, (byte) 0b111001),
    BROWN_111010(MushroomType.BROWN, (byte) 0b111010),
    BROWN_111011(MushroomType.BROWN, (byte) 0b111011),
    BROWN_111100(MushroomType.BROWN, (byte) 0b111100),
    BROWN_111101(MushroomType.BROWN, (byte) 0b111101),
    BROWN_111110(MushroomType.BROWN, (byte) 0b111110),
    BROWN_111111(MushroomType.BROWN, (byte) 0b111111),
    /**
     * @deprecated cannot retexture item with CustomModelData=0 as it's the default value
     */
    @Deprecated
    BROWN_000000(MushroomType.BROWN, (byte) 0b000000),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    BROWN_000110(MushroomType.BROWN, (byte) 0b000110),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    BROWN_010110(MushroomType.BROWN, (byte) 0b010110),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    BROWN_000010(MushroomType.BROWN, (byte) 0b000010),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    BROWN_010010(MushroomType.BROWN, (byte) 0b010010),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    BROWN_011010(MushroomType.BROWN, (byte) 0b011010),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    BROWN_001010(MushroomType.BROWN, (byte) 0b001010),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    BROWN_001011(MushroomType.BROWN, (byte) 0b001011),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    BROWN_000011(MushroomType.BROWN, (byte) 0b000011),

    STEM_000001(MushroomType.STEM, (byte) 0b000001),
    STEM_000010(MushroomType.STEM, (byte) 0b000010),
    STEM_000011(MushroomType.STEM, (byte) 0b000011),
    STEM_000100(MushroomType.STEM, (byte) 0b000100),
    STEM_000101(MushroomType.STEM, (byte) 0b000101),
    STEM_000110(MushroomType.STEM, (byte) 0b000110),
    STEM_000111(MushroomType.STEM, (byte) 0b000111),
    STEM_001000(MushroomType.STEM, (byte) 0b001000),
    STEM_001001(MushroomType.STEM, (byte) 0b001001),
    STEM_001010(MushroomType.STEM, (byte) 0b001010),
    STEM_001011(MushroomType.STEM, (byte) 0b001011),
    STEM_001100(MushroomType.STEM, (byte) 0b001100),
    STEM_001101(MushroomType.STEM, (byte) 0b001101),
    STEM_001110(MushroomType.STEM, (byte) 0b001110),
    STEM_001111(MushroomType.STEM, (byte) 0b001111),
    STEM_010000(MushroomType.STEM, (byte) 0b010000),
    STEM_010001(MushroomType.STEM, (byte) 0b010001),
    STEM_010010(MushroomType.STEM, (byte) 0b010010),
    STEM_010011(MushroomType.STEM, (byte) 0b010011),
    STEM_010100(MushroomType.STEM, (byte) 0b010100),
    STEM_010101(MushroomType.STEM, (byte) 0b010101),
    STEM_010110(MushroomType.STEM, (byte) 0b010110),
    STEM_010111(MushroomType.STEM, (byte) 0b010111),
    STEM_011000(MushroomType.STEM, (byte) 0b011000),
    STEM_011001(MushroomType.STEM, (byte) 0b011001),
    STEM_011010(MushroomType.STEM, (byte) 0b011010),
    STEM_011011(MushroomType.STEM, (byte) 0b011011),
    STEM_011100(MushroomType.STEM, (byte) 0b011100),
    STEM_011110(MushroomType.STEM, (byte) 0b011110),
    STEM_011111(MushroomType.STEM, (byte) 0b011111),
    STEM_100000(MushroomType.STEM, (byte) 0b100000),
    STEM_100001(MushroomType.STEM, (byte) 0b100001),
    STEM_100010(MushroomType.STEM, (byte) 0b100010),
    STEM_100011(MushroomType.STEM, (byte) 0b100011),
    STEM_100100(MushroomType.STEM, (byte) 0b100100),
    STEM_100101(MushroomType.STEM, (byte) 0b100101),
    STEM_100110(MushroomType.STEM, (byte) 0b100110),
    STEM_100111(MushroomType.STEM, (byte) 0b100111),
    STEM_101000(MushroomType.STEM, (byte) 0b101000),
    STEM_101001(MushroomType.STEM, (byte) 0b101001),
    STEM_101010(MushroomType.STEM, (byte) 0b101010),
    STEM_101011(MushroomType.STEM, (byte) 0b101011),
    STEM_101100(MushroomType.STEM, (byte) 0b101100),
    STEM_101101(MushroomType.STEM, (byte) 0b101101),
    STEM_101110(MushroomType.STEM, (byte) 0b101110),
    STEM_101111(MushroomType.STEM, (byte) 0b101111),
    STEM_110000(MushroomType.STEM, (byte) 0b110000),
    STEM_110001(MushroomType.STEM, (byte) 0b110001),
    STEM_110010(MushroomType.STEM, (byte) 0b110010),
    STEM_110011(MushroomType.STEM, (byte) 0b110011),
    STEM_110100(MushroomType.STEM, (byte) 0b110100),
    STEM_110101(MushroomType.STEM, (byte) 0b110101),
    STEM_110110(MushroomType.STEM, (byte) 0b110110),
    STEM_110111(MushroomType.STEM, (byte) 0b110111),
    STEM_111000(MushroomType.STEM, (byte) 0b111000),
    STEM_111001(MushroomType.STEM, (byte) 0b111001),
    STEM_111010(MushroomType.STEM, (byte) 0b111010),
    STEM_111011(MushroomType.STEM, (byte) 0b111011),
    STEM_111100(MushroomType.STEM, (byte) 0b111100),
    STEM_111101(MushroomType.STEM, (byte) 0b111101),
    STEM_111110(MushroomType.STEM, (byte) 0b111110),
    STEM_111111(MushroomType.STEM, (byte) 0b111111),
    /**
     * @deprecated cannot retexture item with CustomModelData=0 as it's the default value
     */
    @Deprecated
    STEM_000000(MushroomType.STEM, (byte) 0b000000),
    /**
     * @deprecated used in vanilla generation
     */
    @Deprecated
    STEM_011101(MushroomType.STEM, (byte) 0b011101);

    MushroomType type;
    byte value;

    Mushroom(MushroomType type, byte value) {
        this.type = type;
        this.value = value;
    }

    /**
     * @param in Custom model data value
     * @return The mushroom value corresponding to the custom model data
     */
    public static Mushroom fromCustomModel(int in) {
        var type = switch (in / 0b111111) {
            case 0 -> MushroomType.RED;
            case 1 -> MushroomType.BROWN;
            case 2 -> MushroomType.STEM;
            default -> throw new IllegalStateException("Unexpected value: " + in / 0b111111);
        };
        var value = in % 0b111111;
        var name = String.format("%s_%06d", type, Integer.parseInt(Integer.toBinaryString(value)));
        return Mushroom.valueOf(name);
    }

    /**
     * @return Custom model data representation of this mushroom
     */
    public int customModelData() {
        return switch (type) {
            case RED -> 0;
            case BROWN -> 1;
            case STEM -> 2;
        } * 0b111111 + value;
    }

    public enum MushroomType {
        RED(Material.RED_MUSHROOM_BLOCK),
        BROWN(Material.BROWN_MUSHROOM_BLOCK),
        STEM(Material.MUSHROOM_STEM);

        private final Material material;

        MushroomType(Material material) {
            this.material = material;
        }

        /**
         * Checks if the given material is a mushroom
         *
         * @param material The material
         * @return true if the material is a mushroom
         */
        public static boolean isMushroom(Material material) {
            return switch (material) {
                case RED_MUSHROOM_BLOCK, BROWN_MUSHROOM_BLOCK, MUSHROOM_STEM -> true;
                default -> false;
            };
        }

        /**
         * @param material The material
         * @return A MushroomType value corresponding to the given Material
         */
        public static MushroomType valueOf(Material material) {
            return switch (material) {
                case RED_MUSHROOM_BLOCK -> RED;
                case BROWN_MUSHROOM_BLOCK -> BROWN;
                case MUSHROOM_STEM -> STEM;
                default -> throw new EnumConstantNotPresentException(MushroomType.class, material.name());
            };
        }

        /**
         * @return The material corresponding to this MushroomType
         */
        public Material getMaterial() {
            return material;
        }
    }
}