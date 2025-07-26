package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FertilityRecordServiceTest {

    @Mock FertilityRecordRepository         fertilityRecordRepository;
    @Mock MicrobiologyResultRepository      microbiologyResultRepository;
    @Mock HormonePanelRepository            hormonePanelRepository;
    @Mock HysterosalpingographyRepository   hysterosalpingographyRepository;
    @Mock PelvicUltrasoundRepository        pelvicUltrasoundRepository;
    @Mock SpermogramRepository              spermogramRepository;
    @Mock MedicalAttachmentRepository       medicalAttachmentRepository;
    @Mock RadiologyReportRepository radiologyReportRepository;
    @Mock SurgicalReportRepository surgicalReportRepository;

    @InjectMocks FertilityRecordService service;

    /* ------------------------------------------------------------------ */

    @Test
    void addFertilityRecord_savesRecord() {
        FertilityRecord record = new FertilityRecord();
        service.addFertilityRecord(record);
        verify(fertilityRecordRepository).save(record);
    }

    /* ------------------------------------------------------------------ */

    @Test
    void getFertilityRecord_returnsRecord() {
        FertilityRecord record = new FertilityRecord();
        when(fertilityRecordRepository.findById("id")).thenReturn(Optional.of(record));

        FertilityRecord result = service.getFertilityRecord("id");

        assertSame(record, result);
    }

    @Test
    void getFertilityRecord_throwsWhenMissing() {
        when(fertilityRecordRepository.findById("id")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getFertilityRecord("id"));
    }

    /* ------------------------------------------------------------------ */

    @Test
    void getFullFertilityRecord_aggregatesData() {
        // master record (id is NULL in default constructor – we leave it that way)
        FertilityRecord record = new FertilityRecord();
        when(fertilityRecordRepository.findById("rid"))
                .thenReturn(Optional.of(record));

        // children
        MicrobiologyResult      micro = MicrobiologyResult     .builder().build();
        HormonePanel            horm  = HormonePanel           .builder().build();
        Hysterosalpingography   hyst  = Hysterosalpingography  .builder().build();
        PelvicUltrasound        pelv  = PelvicUltrasound       .builder().build();
        Spermogram              sper  = Spermogram             .builder().build();
        MedicalAttachment       att   = MedicalAttachment      .builder().build();
        RadiologyReport              radio  = RadiologyReport             .builder().build();
        SurgicalReport       surg   = SurgicalReport      .builder().build();



        when(microbiologyResultRepository    .findByRecordId(nullable(String.class))).thenReturn(List.of(micro));
        when(hormonePanelRepository          .findByRecordId(nullable(String.class))).thenReturn(List.of(horm));
        when(hysterosalpingographyRepository .findByRecordId(nullable(String.class))).thenReturn(List.of(hyst));
        when(pelvicUltrasoundRepository      .findByRecordId(nullable(String.class))).thenReturn(List.of(pelv));
        when(spermogramRepository            .findByRecordId(nullable(String.class))).thenReturn(List.of(sper));
        when(medicalAttachmentRepository     .findByRecordId(nullable(String.class))).thenReturn(List.of(att));
        when(radiologyReportRepository     .findByRecordId(nullable(String.class))).thenReturn(List.of(radio));
        when( surgicalReportRepository    .findByRecordId(nullable(String.class))).thenReturn(List.of(surg));

        FertilityRecordDetails details = service.getFullFertilityRecord("rid");

        assertSame(record, details.getRecord());
        assertEquals(List.of(micro), details.getMicrobiologyResults());
        assertEquals(List.of(horm),  details.getHormonePanels());
        assertEquals(List.of(hyst),  details.getHysterosalpingographies());
        assertEquals(List.of(pelv),  details.getPelvicUltrasounds());
        assertEquals(List.of(sper),  details.getSpermograms());
        assertEquals(List.of(att),   details.getMedicalAttachments());
        assertEquals(List.of(radio),   details.getRadiologyReports());
        assertEquals(List.of(surg),   details.getSurgicalReports());
    }

    @Test
    void getAllRecords_returnsList() {
        FertilityRecord rec1 = new FertilityRecord();
        FertilityRecord rec2 = new FertilityRecord();
        List<FertilityRecord> all = List.of(rec1, rec2);
        when(fertilityRecordRepository.findAll()).thenReturn(all);
        List<FertilityRecord> result = service.getAllRecords();
        assertEquals(all, result);
    }

    @Test
    void createRecordForUser_savesRecordWithId() {
        service.createRecordForUser("user-1");
        verify(fertilityRecordRepository).save(argThat(rec -> "user-1".equals(rec.getId())));
    }

    @Test
    void getSummary_returnsRecordSummary() {
        FertilityRecord rec = FertilityRecord.builder().id("abc").summary("hello").build();
        when(fertilityRecordRepository.findById("abc")).thenReturn(Optional.of(rec));
        assertEquals("hello", service.getSummary("abc"));
    }
}