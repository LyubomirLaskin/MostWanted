package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.TownDto;
import mostwanted.domain.entities.Town;
import mostwanted.repository.TownRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TownServiceImpl implements TownService {

    private final static String TOWN_JSON_FILE_PATH = "E:\\SoftUni\\JavaDatabase\\Projects\\MostWanted\\src\\main\\resources\\files\\towns.json";
    private final TownRepository townRepository;
    private final FileUtil fileUtil;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public TownServiceImpl(TownRepository townRepository, FileUtil fileUtil, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.townRepository = townRepository;
        this.fileUtil = fileUtil;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public Boolean townsAreImported() {
        return this.townRepository.count() != 0;
    }

    @Override
    public String readTownsJsonFile() throws IOException {
        return this.fileUtil.readFile(TOWN_JSON_FILE_PATH);
    }

    @Override
    public String importTowns(String townsFileContent) {
        StringBuilder importer = new StringBuilder();

        TownDto[] townDtos = this.gson.fromJson(townsFileContent,TownDto[].class);

        for (TownDto townDto : townDtos) {
            Town townEntity = this.townRepository.findByName(townDto.getName()).orElse(null);
            if (townEntity != null){
                importer.append(Constants.DUPLICATE_DATA_MESSAGE).append(System.lineSeparator());

                continue;
            }
            if(!this.validationUtil.isValid(townDto)){
                importer.append(Constants.INCORRECT_DATA_MESSAGE).append(System.lineSeparator());

                continue;
            }

            townEntity = this.modelMapper.map(townDto, Town.class);
            this.townRepository.saveAndFlush(townEntity);

            importer.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE, "Town", townEntity.getName())).append(System.lineSeparator());

        }
        return importer.toString().trim();
    }

    @Override
    public String exportRacingTowns() {
        StringBuilder exporter = new StringBuilder();
        List<Town> townsExport= this.townRepository.exportRacingTowns();

        for (Town town : townsExport) {
            exporter.append(String.format("Name: %s",town.getName())).append(System.lineSeparator());
            exporter.append(String.format("Racers: %d",town.getRacers().size())).append(System.lineSeparator());
            exporter.append(System.lineSeparator());
        }
        return exporter.toString().trim();
    }
}
