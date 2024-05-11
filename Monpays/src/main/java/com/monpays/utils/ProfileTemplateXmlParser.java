package com.monpays.utils;

import com.monpays.entities._generic.Operation;
import com.monpays.entities._generic.enums.EOperationType;
import com.monpays.entities.profile.ProfileTemplate;
import com.monpays.entities.profile.enums.EProfileType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProfileTemplateXmlParser {
    public final String xmlPath;
    @Getter
    private final List<ProfileTemplate> profileTemplates;

    public ProfileTemplateXmlParser(@Value("${profile_templates.xml.path}") String xmlPath) {
        this.xmlPath = xmlPath;
        profileTemplates = this.parseXml();
    }

    public List<String> getProfileTemplateNames() {
        return profileTemplates
                .stream()
                .map(profileTemplate -> profileTemplate.getType().toString())
                .toList();
    }

    public Optional<ProfileTemplate> getProfileTemplateByName(String name) {
        return profileTemplates
                .stream()
                .filter(profileTemplate -> profileTemplate.getType().toString().equals(name))
                .findAny();
    }

    private List<ProfileTemplate> parseXml() {
        try {
            File xmlFile = new ClassPathResource(this.xmlPath).getFile();

            jakarta.xml.bind.JAXBContext jaxbCtx = jakarta.xml.bind.JAXBContext.newInstance(ProfileTemplateXmlParser.ProfileTemplates.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            ProfileTemplates profileTemplatesWrapper = (ProfileTemplateXmlParser.ProfileTemplates) unmarshaller.unmarshal(xmlFile);
            return profileTemplatesWrapper.toProfileTemplates();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    @XmlRootElement(name = "profile_templates")
    static class ProfileTemplates {
        private List<ProfileTemplateXml> profileTemplateXmls = new ArrayList<>();

        @XmlElement(name = "profile_template")
        public List<ProfileTemplateXml> getProfileTemplateXmls() {
            return profileTemplateXmls;
        }

        public List<ProfileTemplate> toProfileTemplates() {
            return profileTemplateXmls.stream()
                    .map(ProfileTemplateXml::toProfileTemplate)
                    .toList();
        }

//        @XmlAccessorType(XmlAccessType.FIELD)
        static class ProfileTemplateXml {
            private EProfileType type;
            private Rights rights;

            public void setType(EProfileType type) {
                this.type = type;
            }

            @XmlElement(name = "type")
            public EProfileType getType() {
                return type;
            }

            public void setRights(Rights rights) {
                this.rights = rights;
            }

            @XmlElement(name = "rights")
            public Rights getRights() {
                return rights;
            }

            public ProfileTemplate toProfileTemplate() {
                return new ProfileTemplate(type, rights.toOperationList());
            }
        }


//        @XmlAccessorType(XmlAccessType.FIELD)
        static class Rights {
            private List<Group> groups = new ArrayList<>();

            public void setGroups(List<Group> groups) {
                this.groups = groups;
            }

            @XmlElement(name = "group")
            public List<Group> getGroups() {
                return groups;
            }

            public List<Operation> toOperationList() {
                return groups
                        .stream()
                        .flatMap(group -> group.toOperationList().stream())
                        .toList();
            }
        }

//        @XmlAccessorType(XmlAccessType.FIELD)
        static class Group {
            private String name;
            private OperationTypes operationTypes;

            public void setName(String name) {
                this.name = name;
            }

            @XmlElement(name = "name")
            public String getName() {
                return name;
            }

            public void setOperationTypes(OperationTypes operationTypes) {
                this.operationTypes = operationTypes;
            }

            @XmlElement(name = "operation_types")
            public OperationTypes getOperationTypes() {
                return operationTypes;
            }

            public List<Operation> toOperationList() {
                return operationTypes
                        .getOperationTypes()
                        .stream()
                        .map(operationType -> new Operation(operationType, this.name))
                        .toList();
            }
        }

//        @XmlAccessorType(XmlAccessType.FIELD)
        static class OperationTypes {
            private List<EOperationType> operationTypes = new ArrayList<>();

            public void setOperationTypes(List<EOperationType> operationTypes) {
                this.operationTypes = operationTypes;
            }

            @XmlElement(name = "operation_type")
            public List<EOperationType> getOperationTypes() {
                return operationTypes;
            }
        }
    }
}
