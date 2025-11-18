package com.soskate.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration properties for photo upload and processing.
 * Binds to application.properties with prefix "photo".
 */
@Configuration
@ConfigurationProperties(prefix = "photo")
@Data
public class PhotoConfigProperties {

    private Upload upload = new Upload();
    private Webp webp = new Webp();
    private Full full = new Full();
    private Thumb thumb = new Thumb();
    private Avatar avatar = new Avatar();
    private AvatarThumb avatarThumb = new AvatarThumb();
    private Limits limits = new Limits();

    @Data
    public static class Upload {
        private Long maxFileSize = 10485760L; // 10MB
        private List<String> allowedFormats = List.of("image/jpeg", "image/png", "image/heic", "image/webp");
        private Integer minWidth = 400;
        private Integer minHeight = 400;
        private Double maxRatio = 3.0;
    }

    @Data
    public static class Webp {
        private Integer qualityFull = 85;
        private Integer qualityThumb = 80;
    }

    @Data
    public static class Full {
        private Integer maxWidth = 1920;
    }

    @Data
    public static class Thumb {
        private Integer width = 400;
    }

    @Data
    public static class Avatar {
        private Integer size = 800;
    }

    @Data
    public static class AvatarThumb {
        private Integer size = 200;
    }

    @Data
    public static class Limits {
        private Integer spotGallery = 20;
        private Integer instructorTricks = 50;
        private Integer eventGallery = 10;
    }
}