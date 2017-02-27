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
package bammerbom.ultimatecore.sponge.modules.core.commands.ultimatecore;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.command.HighSubCommand;
import bammerbom.ultimatecore.sponge.api.command.annotations.CommandInfo;
import bammerbom.ultimatecore.sponge.api.command.annotations.CommandParentInfo;
import bammerbom.ultimatecore.sponge.api.command.annotations.CommandPermissions;
import bammerbom.ultimatecore.sponge.api.command.argument.Arguments;
import bammerbom.ultimatecore.sponge.api.command.argument.arguments.PlayerArgument;
import bammerbom.ultimatecore.sponge.api.config.defaultconfigs.datafiles.PlayerDataFile;
import bammerbom.ultimatecore.sponge.api.language.utils.Messages;
import bammerbom.ultimatecore.sponge.api.permission.PermissionLevel;
import bammerbom.ultimatecore.sponge.api.user.UltimateUser;
import bammerbom.ultimatecore.sponge.modules.core.CoreModule;
import bammerbom.ultimatecore.sponge.modules.core.commands.UltimatecoreCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

@CommandPermissions(level = PermissionLevel.OWNER)
@CommandParentInfo(parent = UltimatecoreCommand.class)
@CommandInfo(module = CoreModule.class, aliases = {"resetuser"})
public class ResetuserUltimatecoreCommand implements HighSubCommand {
    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{Arguments.builder(new PlayerArgument(Text.of("player"))).onlyOne().build()};
    }

    @Override
    public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {
        Player t = args.<Player>getOne("player").get();
        UltimateUser user = UltimateCore.get().getUserService().getUser(t);
        //Clear the cache for the user
        user.datas.clear();
        PlayerDataFile file = new PlayerDataFile(user.getIdentifier());
        //Delete the user's file
        file.getFile().delete();
        Messages.send(sender, "core.command.ultimatecore.resetuser.success", "%player%", t);
        return CommandResult.success();
    }
}