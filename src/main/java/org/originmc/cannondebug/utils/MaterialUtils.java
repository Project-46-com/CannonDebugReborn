/*
 * This file is part of CannonProfiler, licensed under the MIT License (MIT).
 *
 * Copyright (c) Origin <http://www.originmc.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.originmc.cannondebug.utils;

import com.google.common.collect.ImmutableList;
import org.bukkit.Material;

import java.util.List;

public final class MaterialUtils {
    private static final List<Material> fallingBlocks = ImmutableList.of(
            Material.WHITE_CONCRETE_POWDER,
            Material.ORANGE_CONCRETE_POWDER,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.LIGHT_BLUE_CONCRETE_POWDER,
            Material.YELLOW_CONCRETE_POWDER,
            Material.LIME_CONCRETE_POWDER,
            Material.PINK_CONCRETE_POWDER,
            Material.GRAY_CONCRETE_POWDER,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.CYAN_CONCRETE_POWDER,
            Material.PURPLE_CONCRETE_POWDER,
            Material.BLUE_CONCRETE_POWDER,
            Material.BROWN_CONCRETE_POWDER,
            Material.GREEN_CONCRETE_POWDER,
            Material.RED_CONCRETE_POWDER,
            Material.BLACK_CONCRETE_POWDER,
            Material.SAND,
            Material.RED_SAND,
            Material.GRAVEL,
            Material.ANVIL);

    /**
     * Identifies whether or not a material is used for dispensing tnt in tnt
     * cannons.
     *
     * @param material the material to identify.
     * @return true if material is used for dispensing.
     */
    public static boolean isDispenser(Material material) {
        return material == Material.DISPENSER;
    }

    /**
     * Identifies whether or not a material is used as explosives with tnt
     * cannons.
     *
     * @param material the material to identify.
     * @return true if material is used as explosives.
     */
    public static boolean isExplosives(Material material) {
        return material == Material.TNT;
    }

    /**
     * Identifies whether or not a material is used for stacking with tnt
     * cannons.
     *
     * @param material the material to identify.
     * @return true if material is used for stacking.
     */
    public static boolean isStacker(Material material) {
        return fallingBlocks.contains(material);
    }

    /**
     * Identifies whether or not a material can be selected using the block
     * selection mode.
     *
     * @param material the material to identify.
     * @param flag     if we should check explosives.
     * @return true if material can be selected.
     */
    public static boolean isSelectable(Material material, boolean flag) {
        return isStacker(material) || isDispenser(material) || flag && isExplosives(material);
    }

}
