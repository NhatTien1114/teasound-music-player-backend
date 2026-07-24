package com.teasound.teasound_api.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSongId implements Serializable {
    private Long playlist;
    private Long song;
}
