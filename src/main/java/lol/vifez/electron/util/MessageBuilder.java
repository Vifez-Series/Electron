package lol.vifez.electron.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageBuilder {
    private final String text;
    private boolean translate;
    private boolean clickable;
    private boolean hover;
    private String hoverText;
    private ClickEvent clickEvent;

    public String getText() {
        return this.text;
    }

    public String getHoverText() {
        return this.hoverText;
    }

    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    public boolean isTranslate() {
        return this.translate;
    }

    public boolean isClickable() {
        return this.clickable;
    }

    public boolean isHover() {
        return this.hover;
    }

    public MessageBuilder(String text) {
        this.text = text;
    }

    public MessageBuilder translate(boolean translate) {
        this.translate = translate;
        return this;
    }

    public MessageBuilder clickable(boolean clickable) {
        this.clickable = clickable;
        return this;
    }

    public MessageBuilder hover(boolean hover) {
        this.hover = hover;
        return this;
    }

    public MessageBuilder hoverText(String text) {
        this.hoverText = text;
        return this;
    }

    public MessageBuilder clickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    public TextComponent textComponent() {
        TextComponent message = new TextComponent(this.translate ? ChatColor.translateAlternateColorCodes('&', this.text) : this.text);
        if (this.hover) {
            HoverEvent.Action action = HoverEvent.Action.SHOW_TEXT;
            TextComponent[] textComponentArr = new TextComponent[1];
            textComponentArr[0] = new TextComponent(this.hoverText == null ? "" : this.hoverText);
            message.setHoverEvent(new HoverEvent(action, textComponentArr));
        }
        if (this.clickable) {
            message.setClickEvent(this.clickEvent);
        }
        return message;
    }

    public void sendMessage(Player player) {
        player.spigot().sendMessage(textComponent());
    }
}
