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
package bammerbom.ultimatecore.sponge.api.command.arguments;

import bammerbom.ultimatecore.sponge.api.command.UCommandElement;
import bammerbom.ultimatecore.sponge.utils.Messages;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class WeatherArgument extends UCommandElement {
    public WeatherArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    public Weather parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String value = args.next();
        switch (value) {
            case "sun":
            case "clear":
                return Weathers.CLEAR;
            case "rain":
            case "snow":
            case "downfall":
                return Weathers.RAIN;
            case "thunder":
            case "thunderstorm":
            case "thunder_storm":
            case "storm":
                return Weathers.THUNDER_STORM;
            default:
                throw args.createError(Messages.getFormatted(source, "weather.command.weather.invalidweathertype", "%weather%", value));
        }
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Arrays.asList("sun", "clear", "rain");
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("sun/rain/thunder");
    }
}
