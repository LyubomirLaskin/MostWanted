package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.DistrictDto;
import mostwanted.domain.entities.District;
import mostwanted.domain.entities.Town;
import mostwanted.repository.DistrictRepository;
import mostwanted.repository.TownRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DistrictServiceImpl implements DistrictService {

    private final static String DISTRICT_JSON_FILE_PATH = "E:\\SoftUni\\JavaDatabase\\Projects\\MostWanted\\src\\main\\resources\\files\\districts.json";
    private final TownRepository townRepository;
    private final DistrictRepository districtRepository;
    private final FileUtil fileUtil;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public DistrictServiceImpl(TownRepository townRepository, DistrictRepository districtRepository, FileUtil fileUtil, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.townRepository = townRepository;
        this.districtRepository = districtRepository;
        this.fileUtil = fileUtil;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public Boolean districtsAreImported() {
        return this.districtRepository.count() != 0;
    }

    @Override
    public String readDistrictsJsonFile() throws IOException {
        return this.fileUtil.readFile(DISTRICT_JSON_FILE_PATH);
    }

    @Override
    public String importDistricts(String districtsFileContent) {
        StringBuilder importer = new StringBuilder();

        DistrictDto[] districtDtos = this.gson.fromJson(districtsFileContent, DistrictDto[].class);

        for (DistrictDto districtDto : districtDtos) {
            District district = this.districtRepository.findByName(districtDto.getName()).orElse(null);
            if (district != null){
                importer.append(Constants.DUPLICATE_DATA_MESSAGE).append(System.lineSeparator());

                continue;
            }

            Town townEntity = this.townRepository.findByName(districtDto.getTownName()).orElse(null);

            if (!this.validationUtil.isValid(districtDto) || townEntity == null){
                importer.append(Constants.INCORRECT_DATA_MESSAGE).append(System.lineSeparator());

                continue;
            }

            district = this.modelMapper.map(districtDto, District.class);
            district.setTown(townEntity);
            this.districtRepository.saveAndFlush(district);

            importer.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE, "District", district.getName())).append(System.lineSeparator());
        }
        return importer.toString().trim();
    }
}
