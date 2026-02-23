package gg.harvester.igdb.theme;

import gg.harvester.igdb.SimpleDTO;
import gg.harvester.igdb.SimpleEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "theme")
public class Theme extends SimpleEntity {

    public String slug;

    private void fulfillSlug() {
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(this.name);
        if  (matcher.find()) {
            this.slug = matcher.group(1);
            this.name = this.name.substring(0, matcher.start()).trim();
        }
    }

    public static Theme parseDTO(SimpleDTO dto) {
        Theme theme = SimpleEntity.parseDTO(dto, Theme::new);
        theme.fulfillSlug();
        return theme;
    }
}
