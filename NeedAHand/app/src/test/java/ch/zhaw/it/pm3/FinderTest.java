package ch.zhaw.it.pm3;

import ch.zhaw.it.pm3.databaseHandling.DataManager;
import ch.zhaw.it.pm3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinderTest {
    private DataManager dataManager;

    private List<Advertisement> advertisementList;
    private List<User> userList;

    private ServiceProvider serviceprovider;

    private Advertisement advertisement1;
    private Advertisement advertisement2;
    private Advertisement advertisement3;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        dataManager = DataManager.getInstance();
        userList = new ArrayList<>();
        advertisementList = new ArrayList<>();

        user1 = new User(createUserInfo1(), dataManager.getNewId(userList, User::getId));
        user2 = new User(createUserInfo2(), dataManager.getNewId(userList, User::getId));
        serviceprovider = new ServiceProvider(createUserInfo3(), dataManager.getNewId(dataManager.getUserList(), User::getId), 0.0);

        userList.add(user1);
        userList.add(user2);

        advertisement1 = new Advertisement(createDocumentInfo4Ad1(), dataManager.getNewId(advertisementList, Advertisement::getId), user1.getId());
        advertisement2 = new Advertisement(createDocumentInfo4Ad2(), dataManager.getNewId(advertisementList, Advertisement::getId), user2.getId());
        advertisement3 = new Advertisement(createDocumentInfo4Ad3(), dataManager.getNewId(advertisementList, Advertisement::getId), user2.getId());
        advertisementList.add(advertisement1);
        advertisementList.add(advertisement2);
        advertisementList.add(advertisement3);
    }

    private UserInfo createUserInfo1() {
        UserInfo userInfo1 = new UserInfo();
        userInfo1.firstname = "Bruce";
        userInfo1.surname = "Lee";
        userInfo1.mailAddress = "bruce.lee@gmail.com";
        userInfo1.password = "Passwort123!";
        userInfo1.city = "St. Gallen";
        userInfo1.street = "Technikumsstrasse";
        userInfo1.postalCode = 8000;
        userInfo1.companyName = null;
        userInfo1.skill = null;
        userInfo1.lengthExperience = null;
        return userInfo1;
    }

    private UserInfo createUserInfo2() {
        UserInfo userInfo2 = new UserInfo();
        userInfo2.firstname = "Jean-Claude";
        userInfo2.surname = "Van Damme";
        userInfo2.mailAddress = "jean-claude.vandamme@gmail.com";
        userInfo2.password = "JeanClaude123#";
        userInfo2.city = "Winterthur";
        userInfo2.street = "Heinistrasse";
        userInfo2.postalCode = 8003;
        userInfo2.companyName = null;
        userInfo2.skill = null;
        userInfo2.lengthExperience = null;
        return userInfo2;
    }

    private UserInfo createUserInfo3() {
        UserInfo userInfo3 = new UserInfo();
        userInfo3.firstname = "Firstname";
        userInfo3.surname = "Surname";
        userInfo3.mailAddress = "mail@gmail.com";
        userInfo3.password = "Passwort123!";
        userInfo3.city = "Zürich";
        userInfo3.street = "Utoquai 69";
        userInfo3.postalCode = 8000;
        userInfo3.companyName = "Sutter AG";
        userInfo3.skill = Skills.CONSTRUCTION_WORK;
        userInfo3.lengthExperience = "08/2000";
        return userInfo3;
    }

    private DocumentInfo createDocumentInfo4Ad1() {
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.title = "Advertisement 1";
        documentInfo.description = getDescription();

        documentInfo.firstname = "Bruce";
        documentInfo.surname = "Lee";
        documentInfo.street = "Technikumsstrasse";
        documentInfo.postalCode = 8003;
        documentInfo.price = 10000.50;
        documentInfo.skill = Skills.CONSTRUCTION_WORK;

        return documentInfo;
    }

    private DocumentInfo createDocumentInfo4Ad2() {
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.title = "Advertisement 2";
        documentInfo.description = getDescription();

        documentInfo.firstname = "Jean-Claude";
        documentInfo.surname = "Van Damme";
        documentInfo.street = "Heinistrasse";
        documentInfo.postalCode = 8003;
        documentInfo.price = 12345.50;
        documentInfo.skill = Skills.SANITARY;

        return documentInfo;
    }

   private DocumentInfo createDocumentInfo4Ad3() {
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.title = "Advertisement 3";
        documentInfo.description = getDescription();

        documentInfo.firstname = "Jean-Claude";
        documentInfo.surname = "Van Damme";
        documentInfo.street = "Heinistrasse";
        documentInfo.postalCode = 9424;
        documentInfo.price = 12345.50;
        documentInfo.skill = Skills.CONSTRUCTION_WORK;

        return documentInfo;
    }

    private String getDescription() {
        return "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod " +
                "tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam " +
                "et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum" +
                "dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
                "sed diam nonumy eirmod tempor";
    }

    @Test
    void testFindAdvertisementMatch() {
        List<Advertisement> suitableAdvertisements = Finder.findAdvertisements(advertisementList, serviceprovider);

        assertEquals(advertisement1, suitableAdvertisements.get(0));
    }

    @Test
    void testFindAdvertisementCheckId() {
        List<Advertisement> suitableAdvertisements = Finder.findAdvertisements(advertisementList, serviceprovider);

        assertEquals(suitableAdvertisements.get(0).getCustomerId(), user1.getId());
    }

    @Test
    void testFindAdvertisementEmpty() {
        advertisementList.remove(0); //removed matching Advertisement

        List<Advertisement> suitableAdvertisements = Finder.findAdvertisements(advertisementList, serviceprovider);

        assertEquals(0, suitableAdvertisements.size());
    }
}
