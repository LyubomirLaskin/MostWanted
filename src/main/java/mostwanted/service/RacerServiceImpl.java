package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.RacerDto;
import mostwanted.domain.entities.Car;
import mostwanted.domain.entities.Racer;
import mostwanted.domain.entities.Town;
import mostwanted.repository.RacerRepository;
import mostwanted.repository.TownRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RacerServiceImpl implements RacerService {

    private final static String RACER_JSON_FILE_PATH = "E:\\SoftUni\\JavaDatabase\\Projects\\MostWanted\\src\\main\\resources\\files\\racers.json";
    private final RacerRepository racerRepository;
    private final TownRepository townRepository;
    private final FileUtil fileUtil;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public RacerServiceImpl(RacerRepository racerRepository, TownRepository townRepository, FileUtil fileUtil, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.racerRepository = racerRepository;
        this.townRepository = townRepository;
        this.fileUtil = fileUtil;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public Boolean racersAreImported() {
        return this.racerRepository.count() != 0;
    }

    @Override
    public String readRacersJsonFile() throws IOException {
        return this.fileUtil.readFile(RACER_JSON_FILE_PATH);
    }

    @Override
    public String importRacers(String racersFileContent) {
        StringBuilder importer = new StringBuilder();

        RacerDto[] racerDtos = this.gson.fromJson(racersFileContent, RacerDto[].class);

        for (RacerDto racerDto : racerDtos) {
            Racer racerEntity = this.racerRepository.findByName(racerDto.getName()).orElse(null);

            if (racerEntity != null){
                importer.append(Constants.DUPLICATE_DATA_MESSAGE).append(System.lineSeparator());
                continue;
            }

            Town townEntity = this.townRepository.findByName(racerDto.getTownName()).orElse(null);

            if (!this.validationUtil.isValid(racerDto) || townEntity == null){
                importer.append(Constants.INCORRECT_DATA_MESSAGE).append(System.lineSeparator());
                continue;
            }

            racerEntity = this.modelMapper.map(racerDto, Racer.class);
            racerEntity.setHomeTown(townEntity);
            this.racerRepository.saveAndFlush(racerEntity);

            importer.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE, "Racer", racerEntity.getName())).append(System.lineSeparator());

        }
        return importer.toString().trim();
    }

    @Override
    public String exportRacingCars() {
        StringBuilder exporter = new StringBuilder();
        List<Racer> racersExport = this.racerRepository.exportRacingCars();

        for (Racer racer : racersExport) {
            exporter.append(String.format("Name: %s", racer.getName())).append(System.lineSeparator());
            if (racer.getAge() != null){
                exporter.append(String.format("Age: %d", racer.getAge())).append(System.lineSeparator());
            }
            exporter.append("Cars:").append(System.lineSeparator());
            for (Car car : racer.getCars()) {
                exporter.append(String.format("%s %s %d",car.getBrand(), car.getModel(), car.getYearOfProduction())).append(System.lineSeparator());
            }
            exporter.append(System.lineSeparator());
        }
        return exporter.toString().trim();
    }
}
