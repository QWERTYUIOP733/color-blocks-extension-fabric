/*
 * Copyright (c) 2026 Color Blocks Extension
 * SPDX-License-Identifier: MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mard.pixel.fabric;

/**
 * 颜色定义类
 * 包含颜色编号、十六进制颜色值、RGB值和色系
 */
public class ColorDefinition {
    private final String code;
    private final String hex;
    private final String series;
    private final int red;
    private final int green;
    private final int blue;

    public ColorDefinition(String code, String hex, String series) {
        this.code = code;
        this.hex = hex;
        this.series = series;
        // 解析十六进制颜色值为RGB
        String cleanHex = hex.replace("#", "");
        this.red = Integer.parseInt(cleanHex.substring(0, 2), 16);
        this.green = Integer.parseInt(cleanHex.substring(2, 4), 16);
        this.blue = Integer.parseInt(cleanHex.substring(4, 6), 16);
    }

    public String getCode() {
        return code;
    }

    public String getHex() {
        return hex;
    }

    public String getSeries() {
        return series;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    /**
     * 获取用于tintindex的颜色值（0xRRGGBB格式）
     */
    public int getColorValue() {
        return (red << 16) | (green << 8) | blue;
    }

    /**
     * 获取物品名称（色号 + RGB值）
     */
    public String getItemName() {
        return code + " (#" + String.format("%02X%02X%02X", red, green, blue) + ")";
    }
}
