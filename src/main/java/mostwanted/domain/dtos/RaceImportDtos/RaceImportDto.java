package mostwanted.domain.dtos.RaceImportDtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "race")
@XmlAccessorType(XmlAccessType.FIELD)
public class RaceImportDto {

    @XmlElement(name = "laps")
    private Integer laps;
    @XmlElement(name = "district-name")
    private String districtName;
    @XmlElement(name = "entries")
    private EntriesRootDto entriesRootDtos;

    public RaceImportDto() {
    }

    @NotNull
    public Integer getLaps() {
        return laps;
    }

    public void setLaps(Integer laps) {
        this.laps = laps;
    }

    @NotNull
    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public EntriesRootDto getEntriesRootDtos() {
        return entriesRootDtos;
    }

    public void setEntriesRootDtos(EntriesRootDto entriesRootDtos) {
        this.entriesRootDtos = entriesRootDtos;
    }
}
