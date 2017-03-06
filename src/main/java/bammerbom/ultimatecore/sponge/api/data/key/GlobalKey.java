/*
 * This file is part of UltimateCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) Bammerbom
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
package bammerbom.ultimatecore.sponge.api.data.key;

import com.google.common.reflect.TypeToken;

import java.util.Optional;

public class GlobalKey<C> implements Key<C> {

    //General info
    private String id;
    private C def;

    //Storage info
    private String storagelocation;
    private TypeToken token;

    public GlobalKey(String id, C def, String storagelocation, TypeToken<C> token) {
        this.id = id;
        this.def = def;
        this.storagelocation = storagelocation;
        this.token = token;
    }

    @Override
    public String getIdentifier() {
        return this.id;
    }

    @Override
    public Optional<C> getDefault() {
        return Optional.ofNullable(this.def);
    }

    @Override
    public String getStorageLocation() {
        return this.storagelocation;
    }

    @Override
    public TypeToken<C> getToken() {
        return this.token;
    }
}
