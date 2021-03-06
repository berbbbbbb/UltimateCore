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
package bammerbom.ultimatecore.sponge.api.variable.utils;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.language.utils.Messages;
import bammerbom.ultimatecore.sponge.api.module.Modules;
import bammerbom.ultimatecore.sponge.api.user.UltimateUser;
import bammerbom.ultimatecore.sponge.modules.nick.api.NickKeys;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VariableUtil {
    public static Text replaceVariables(Text text, @Nullable Object player) {
        return UltimateCore.get().getVariableService().replace(text, player);
    }

    public static Text getNameSource(CommandSource player) {
        //TODO nickname
        if (player instanceof Player) {
            Player p = (Player) player;
            Text name = ((Player) player).getDisplayNameData().displayName().get();
            if (Modules.NICK.isPresent()) {
                UltimateUser up = UltimateCore.get().getUserService().getUser(p);
                if (up.get(NickKeys.NICKNAME).isPresent() && !up.get(NickKeys.NICKNAME).get().isEmpty()) {
                    name = up.get(NickKeys.NICKNAME).get();
                }
            }
            return name.toBuilder().onHover(TextActions.showText(Messages.getFormatted("core.variable.player.hover", "%name%", name, "%rawname%", player.getName(), "%uuid%", ((Player) player).getUniqueId(), "%language%", player.getLocale().getDisplayName(Locale.ENGLISH)))).onClick(TextActions.suggestCommand(Messages.getFormatted("core.variable.player.click", "%player%", player.getName()).toPlain())).build();
        } else {
            return Text.builder(player.getName()).onHover(TextActions.showText(Messages.getFormatted("core.variable.player.hover", "%name%", player.getName(), "%rawname%", player.getName(), "%uuid%", player.getIdentifier(), "%language%", player.getLocale().getDisplayName(Locale.ENGLISH)))).onClick(TextActions.suggestCommand(Messages.getFormatted("core.variable.player.click", "%player%", player.getName()).toPlain())).build();
        }
    }

    public static Text getNameEntity(Entity en) {
        if (en instanceof CommandSource) {
            return getNameSource((CommandSource) en);
        } else {
            return Text.of(en.getTranslation().get(Locale.ENGLISH));
        }
    }

    public static Text getNamesEntity(List<? extends Entity> ens) {
        List<Text> texts = new ArrayList<>();
        for (Entity en : ens) {
            texts.add(getNameEntity(en));
        }
        return Text.joinWith(Text.of(", "), texts);
    }

    public static Text getNameUser(User player) {
        if (player instanceof Player) {
            return getNameSource((CommandSource) player);
        }
        //TODO language?
        return Text.builder(player.getName()).onHover(TextActions.showText(Messages.getFormatted("core.variable.player.hover", "%name%", player.getName(), "%rawname%", player.getName(), "%uuid%", player.getUniqueId(), "%language%", "?"))).onClick(TextActions.suggestCommand(Messages.getFormatted("core.variable.player.click", "%player%", player.getName()).toPlain())).build();
    }
}
