package com.example.streammateseriessvc.app.commonData.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CinemaRecordResponse {
    private UUID id;
    private String title;
    private String posterImgURL;
    private String releaseDate;
}
