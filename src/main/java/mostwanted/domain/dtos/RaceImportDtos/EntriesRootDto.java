package mostwanted.domain.dtos.RaceImportDtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entries")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntriesRootDto {

    @XmlElement(name = "entry")
    private EntriesDto[] entriesDtos;

    public EntriesRootDto() {
    }

    public EntriesDto[] getEntriesDtos() {
        return entriesDtos;
    }

    public void setEntriesDtos(EntriesDto[] entriesDtos) {
        this.entriesDtos = entriesDtos;
    }
}
