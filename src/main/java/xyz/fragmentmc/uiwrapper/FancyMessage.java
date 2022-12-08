package xyz.fragmentmc.uiwrapper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FancyMessage {
    private boolean sent = false;
    private TextComponent textComponent = Component.text("");
    private String text;
    private ChatColor color = ChatColor.WHITE;
    private final List<ChatColor> styles = new ArrayList<>();
    private ClickEvent clickEvent = null;
    private List<FancyMessage> tooltip = new ArrayList<>();

    public FancyMessage(String text) {
        this.text = text;
    }

    private void next() {
        // text
        TextComponent component = Component.text(this.text);

        // color
        LegacyFormat color = LegacyComponentSerializer.parseChar(this.color.getChar());
        this.color = ChatColor.WHITE;
        if (color != null) component = component.color(color.color());

        // styles
        if (this.styles.size() > 0) {
            for (ChatColor s : this.styles) {
                LegacyFormat style = LegacyComponentSerializer.parseChar(s.getChar());
                if (style != null && style.decoration() != null) component = component.decorate(style.decoration());
            }
            this.styles.clear();
        }

        // clickEvent
        if (this.clickEvent != null) {
            component = component.clickEvent(this.clickEvent);
            this.clickEvent = null;
        }

        // tooltip
        if (this.tooltip.size() > 0) {
            this.tooltip.get(0).end();
            Component tooltip = this.tooltip.get(0).textComponent;
            this.tooltip.remove(0);
            for (FancyMessage m : this.tooltip) {
                m.end();
                tooltip = tooltip.append(Component.newline()).append(m.textComponent);
            }
            component = component.hoverEvent(HoverEvent.showText(tooltip));
            this.tooltip.clear();
        }

        //finalize
        append(component);
    }

    public FancyMessage then(String text) {
        next();
        this.text = text;
        return this;
    }

    public FancyMessage color(ChatColor color) {
        if (!color.isColor()) throw new IllegalArgumentException(color.name() + " is not a color");
        this.color = color;
        return this;
    }

    public FancyMessage style(ChatColor... styles) {
        for (ChatColor s : styles) {
            if (!s.isFormat()) throw new IllegalArgumentException(s.name() + " is not a style");
        }
        this.styles.addAll(Arrays.asList(styles));
        return this;
    }

    public FancyMessage command(String command) {
        this.clickEvent = ClickEvent.runCommand(command);
        return this;
    }

    public FancyMessage suggest(String command) {
        this.clickEvent = ClickEvent.suggestCommand(command);
        return this;
    }

    public FancyMessage formattedTooltip(FancyMessage... tooltips) {
        this.tooltip.addAll(Arrays.asList(tooltips));
        return this;
    }

    public void end() {
        if (!sent) {
            sent = true;
            next();
        }
    }

    public void send(CommandSender sender) {
        end();
        sender.sendMessage(this.textComponent);
    }

    private void append(TextComponent component) {
        this.textComponent = this.textComponent.append(component);
    }
}
