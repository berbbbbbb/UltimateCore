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
package bammerbom.ultimatecore.sponge.modules.random.commands;

import bammerbom.ultimatecore.sponge.api.command.Arguments;
import bammerbom.ultimatecore.sponge.api.command.RegisterCommand;
import bammerbom.ultimatecore.sponge.api.command.SmartCommand;
import bammerbom.ultimatecore.sponge.api.command.arguments.IntegerArgument;
import bammerbom.ultimatecore.sponge.api.permission.Permission;
import bammerbom.ultimatecore.sponge.modules.random.RandomModule;
import bammerbom.ultimatecore.sponge.modules.random.api.RandomPermissions;
import bammerbom.ultimatecore.sponge.utils.Messages;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RegisterCommand(module = RandomModule.class, aliases = {"random"})
public class RandomCommand implements SmartCommand {
    static Random random = new Random();

    @Override
    public Permission getPermission() {
        return RandomPermissions.UC_RANDOM_RANDOM_BASE;
    }

    @Override
    public List<Permission> getPermissions() {
        return Arrays.asList(RandomPermissions.UC_RANDOM_RANDOM_BASE);
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                Arguments.builder(new IntegerArgument(Text.of("first"))).usage("<Min>").onlyOne().build(),
                Arguments.builder(new IntegerArgument(Text.of("second"))).usage("[Max]").onlyOne().optional().build()
        };
    }

    @Override
    public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {
        if (!sender.hasPermission(RandomPermissions.UC_RANDOM_RANDOM_BASE.get())) {
            sender.sendMessage(Messages.getFormatted(sender, "core.nopermissions"));
            return CommandResult.empty();
        }

        int min = args.hasAny("second") ? args.<Integer>getOne("first").get() : 1;
        int max = args.hasAny("second") ? args.<Integer>getOne("second").get() : args.<Integer>getOne("first").get();
        if (min > max) {
            sender.sendMessage(Messages.getFormatted(sender, "random.command.random.invalid", "%min%", min, "%max%", max));
            return CommandResult.empty();
        }
        int rand = random.nextInt((max + 1) - min) + min;

        sender.sendMessage(Messages.getFormatted(sender, "random.command.random.success", "%min%", min, "%max%", max, "%value%", rand));
        return CommandResult.success();
    }
}
