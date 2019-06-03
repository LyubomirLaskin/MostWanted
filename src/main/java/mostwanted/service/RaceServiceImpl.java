package mostwanted.service;

import mostwanted.common.Constants;
import mostwanted.domain.dtos.RaceImportDtos.EntriesDto;
import mostwanted.domain.dtos.RaceImportDtos.RaceImportDto;
import mostwanted.domain.dtos.RaceImportDtos.RaceImportRootDto;
import mostwanted.domain.entities.District;
import mostwanted.domain.entities.Race;
import mostwanted.domain.entities.RaceEntry;
import mostwanted.repository.DistrictRepository;
import mostwanted.repository.RaceEntryRepository;
import mostwanted.repository.RaceRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import mostwanted.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RaceServiceImpl implements RaceService{

    private final static String RACE_XML_FILE_PATH = "E:\\SoftUni\\JavaDatabase\\Projects\\MostWanted\\src\\main\\resources\\files\\races.xml";
    private final RaceRepository raceRepository;
    private final FileUtil fileUtil;
    private final DistrictRepository districtRepository;
    private final RaceEntryRepository raceEntryRepository;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public RaceServiceImpl(RaceRepository raceRepository, FileUtil fileUtil, DistrictRepository districtRepository, RaceEntryRepository raceEntryRepository, XmlParser xmlParser, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.raceRepository = raceRepository;
        this.fileUtil = fileUtil;
        this.districtRepository = districtRepository;
        this.raceEntryRepository = raceEntryRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public Boolean racesAreImported() {
        return this.raceRepository.count() != 0;
    }

    @Override
    public String readRacesXmlFile() throws IOException {
        return this.fileUtil.readFile(RACE_XML_FILE_PATH);
    }

    @Override
    public String importRaces() throws JAXBException {
        StringBuilder importer = new StringBuilder();

        RaceImportRootDto raceImportRootDto = this.xmlParser.parseXml(RaceImportRootDto.class, RACE_XML_FILE_PATH);

        for (RaceImportDto raceImportDto : raceImportRootDto.getRaceImportDtos()) {
            District districtEntity = this.districtRepository.findByName(raceImportDto.getDistrictName()).orElse(null);
            if (!this.validationUtil.isValid(raceImportDto) || districtEntity == null){
                importer.append(Constants.INCORRECT_DATA_MESSAGE).append(System.lineSeparator());
                continue;
            }


            Race raceEntity = this.modelMapper.map(raceImportDto, Race.class);
            raceEntity.setDistrict(districtEntity);

            List<RaceEntry> raceEntries = new ArrayList<>();
            for (EntriesDto entriesDto : raceImportDto.getEntriesRootDtos().getEntriesDtos()) {
                RaceEntry raceEntryEntity = this.raceEntryRepository.findById(entriesDto.getId()).orElse(null);
                if (raceEntryEntity == null){
                    continue;
                }
                raceEntryEntity.setRace(raceEntity);
                raceEntries.add(raceEntryEntity);
            }
            raceEntity = this.raceRepository.saveAndFlush(raceEntity);
            this.raceEntryRepository.saveAll(raceEntries);

            importer.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,"Race",raceEntity.getId())).append(System.lineSeparator());

        }
        return importer.toString().trim();
    }
}
