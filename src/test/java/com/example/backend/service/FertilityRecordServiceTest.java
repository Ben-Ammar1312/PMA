import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FertilityRecordServiceTest {

    @Mock
    FertilityRecordRepository fertilityRecordRepository;
    @Mock
    BacteriologyAnalysisRepository bacteriologyAnalysisRepository;
    @Mock
    HormonePanelRepository hormonePanelRepository;
    @Mock
    HysterosalpingographyRepository hysterosalpingographyRepository;
    @Mock
    PelvicUltrasoundRepository pelvicUltrasoundRepository;
    @Mock
    SpermogramRepository spermogramRepository;
    @Mock
    MedicalAttachmentRepository medicalAttachmentRepository;

    @InjectMocks
    FertilityRecordService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFertilityRecord_savesRecord() {
        FertilityRecord record = new FertilityRecord();
        service.addFertilityRecord(record);
        verify(fertilityRecordRepository).save(record);
    }

    @Test
    void getFertilityRecord_returnsRecord() {
        FertilityRecord record = new FertilityRecord();
        when(fertilityRecordRepository.findById("id"))
                .thenReturn(Optional.of(record));

        FertilityRecord result = service.getFertilityRecord("id");
        assertSame(record, result);
    }

    @Test
    void getFertilityRecord_throwsWhenMissing() {
        when(fertilityRecordRepository.findById("id"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getFertilityRecord("id"));
    }

    @Test
    void getFullFertilityRecord_aggregatesData() {
        FertilityRecord record = new FertilityRecord();
        when(fertilityRecordRepository.findById("id"))
                .thenReturn(Optional.of(record));
        BacteriologyAnalysis b = BacteriologyAnalysis.builder().build();
        HormonePanel h = HormonePanel.builder().build();
        Hysterosalpingography hy = Hysterosalpingography.builder().build();
        PelvicUltrasound p = PelvicUltrasound.builder().build();
        Spermogram s = Spermogram.builder().build();
        MedicalAttachment m = MedicalAttachment.builder().build();

        when(bacteriologyAnalysisRepository.findByRecordId("id"))
                .thenReturn(List.of(b));
        when(hormonePanelRepository.findByRecordId("id"))
                .thenReturn(List.of(h));
        when(hysterosalpingographyRepository.findByRecordId("id"))
                .thenReturn(List.of(hy));
        when(pelvicUltrasoundRepository.findByRecordId("id"))
                .thenReturn(List.of(p));
        when(spermogramRepository.findByRecordId("id"))
                .thenReturn(List.of(s));
        when(medicalAttachmentRepository.findByRecordId("id"))
                .thenReturn(List.of(m));

        FertilityRecordDetails details = service.getFullFertilityRecord("id");

        assertSame(record, details.getRecord());
        assertEquals(List.of(b), details.getBacteriologyAnalyses());
        assertEquals(List.of(h), details.getHormonePanels());
        assertEquals(List.of(hy), details.getHysterosalpingographies());
        assertEquals(List.of(p), details.getPelvicUltrasounds());
        assertEquals(List.of(s), details.getSpermograms());
        assertEquals(List.of(m), details.getMedicalAttachments());
    }
}
