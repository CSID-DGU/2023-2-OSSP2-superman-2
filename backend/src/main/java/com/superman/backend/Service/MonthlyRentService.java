package com.superman.backend.Service;

import com.superman.backend.Entity.SessionData;
import com.superman.backend.Repository.MonthlyRentRepository;
import com.superman.backend.Repository.SessionDataRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.ErrorManager;
import java.util.stream.Collectors;

@Service
public class MonthlyRentService {
    private static final Logger logger = LoggerFactory.getLogger(TransportCostService.class);

    @Autowired
    MonthlyRentRepository monthlyRentRepository;
    @Autowired
    SessionDataRepository sessionDataRepository;
    @Autowired
    TransportCostService transportCostService;
    @Autowired
    TravalTimeService travalTimeService;
    public List<?> findMonthlyRentByCost(int region_id, int range, int maxtraval, String userid){
        int min = 0, max = 0;
        String X; String Y;  int transportType; 
        switch (range) {
            case 1:
                min = 0;
                max = 20;
                break;
            case 2:
                min = 20;
                max = 40;
                break;
            case 3:
                min = 40;
                max = 60;
                break;
            case 4:
                min = 60;
                max = 80;
                break;
            case 5:
                min = 80;
                max = 100;
                break;
            case 6:
                min = 100;
                max = 20000;
                break;

        }

        SessionData existingData = sessionDataRepository.findById(userid).orElse(null);
        if (existingData != null) {
            X = existingData.getOftenPlaceX();
            Y = existingData.getOftenPlaceY();
            transportType = existingData.getTransportationType();
        }else{
            List<String> errorResult = new ArrayList<>();
            errorResult.add("유효하지 않은 사용자 아이디입니다. 올바른 사용자 아이디를 입력해주세요.");
            return errorResult;
        }
        List<Object[]> data;
        if(region_id == 11000)
            data = (List<Object[]>) monthlyRentRepository.getMonthlyRentDataByCostALL(min, max);
        else
            data = (List<Object[]>) monthlyRentRepository.getMonthlyRentDataByCost(region_id, min, max);
        List<RentDetails> filteredTop5 = new ArrayList<>();

        for (Object[] item : data) {
            try {
                String place = (String) item[0]; // 동 이름 (예를 들어, 동 이름이 첫 번째 열이라 가정)
                int logical_code = Integer.parseInt(item[0].toString());
                if(region_id == 11000) place = lawdCodeToDistrictMap.get(logical_code);
                else place = (String) item[0];

                double area = (double) item[1];
                double cost = (double) item[2];

                String[] coordinates = travalTimeService.getCoordinates("서울시 " + place);
                String start, goal;
                int time;int transportcost = 0;
                try {
                    if (transportType == 2) {
                        time = Integer.parseInt(travalTimeService.sendCarTimeRequest(coordinates[0], coordinates[1], X, Y));
                        start = X + ", " + Y;
                        goal = coordinates[0] + ", " + Y;
                        transportcost = transportCostService.getCarCost(start, goal, 14.0);
                    } else if (transportType == 1) {
                        time = Integer.parseInt(travalTimeService.sendTransportTimeRequest(coordinates[0], coordinates[1], X, Y));
                    } else {
                        throw new RuntimeException("유저 교통 정보 없음.");
                    }
                }
                catch (Exception e) {
                    logger.error("교통 실패", e);
                    throw new RuntimeException("교통 실패: " + e.getMessage());
                }
                int hours = time / 3600;
                int minutes = (time % 3600) / 60;
                RentDetails rentDetails = new RentDetails();

                rentDetails.setPlace(place);
                rentDetails.setArea(area + "평");
                rentDetails.setCost((int)cost + "만원");
                rentDetails.setTransportcost(String.valueOf((transportcost)));
                rentDetails.setTime(hours + "시간 " + minutes + "분");

                filteredTop5.add(rentDetails);

                if (filteredTop5.size() >= 5) {
                    break; // 상위 5개만 필터링하므로 5개 이상이면 종료
                }
            } catch (Exception e) {
                logger.error("카카오 API 연결 실패", e);
                throw new RuntimeException("카카오 API 연결 실패: " + e.getMessage());
            }
        }

        return filteredTop5;
    }

    public List<?> findMonthlyRentByArea(int region_id, int range, int maxtraval, String userid){
        int min = 0, max = 0;
        String X; String Y;  int transportType;
        switch (range) {
            case 1:
                min = 0;
                max = 20;
                break;
            case 2:
                min = 20;
                max = 40;
                break;
            case 3:
                min = 40;
                max = 60;
                break;
            case 4:
                min = 60;
                max = 80;
                break;
            case 5:
                min = 80;
                max = 100;
                break;
            case 6:
                min = 100;
                max = 20000;
                break;

        }

        SessionData existingData = sessionDataRepository.findById(userid).orElse(null);
        if (existingData != null) {
            X = existingData.getOftenPlaceX();
            Y = existingData.getOftenPlaceY();
            transportType = existingData.getTransportationType();
        }else{
            List<String> errorResult = new ArrayList<>();
            errorResult.add("유효하지 않은 사용자 아이디입니다. 올바른 사용자 아이디를 입력해주세요.");
            return errorResult;
        }
        List<Object[]> data;
        if(region_id == 11000)
             data = (List<Object[]>) monthlyRentRepository.getMonthlyRentDataByAreaALL(min, max);
        else
             data = (List<Object[]>) monthlyRentRepository.getMonthlyRentDataByArea(region_id, min, max);
        List<RentDetails> filteredTop5 = new ArrayList<>();

        for (Object[] item : data) {
            try {
                String place;
                int logical_code = Integer.parseInt(item[0].toString());
                if(region_id == 11000) place = lawdCodeToDistrictMap.get(logical_code);
                else place = (String) item[0];

                double cost = (double) item[1];
                double area = (double) item[2];
                String[] coordinates = travalTimeService.getCoordinates("서울시 " + place);
                String start, goal;
                int time; int transportcost = 0;
                try {
                    if (transportType == 2) {
                        time = Integer.parseInt(travalTimeService.sendCarTimeRequest(coordinates[0], coordinates[1], X, Y));
                        start = X + ", " + Y;
                        goal = coordinates[0] + ", " + Y;
                        transportcost = transportCostService.getCarCost(start, goal, 14.0);
                    } else if (transportType == 1) {
                        time = Integer.parseInt(travalTimeService.sendTransportTimeRequest(coordinates[0], coordinates[1], X, Y));
                    } else {
                        throw new RuntimeException("유저 교통 정보 없음.");
                    }
                }
                catch (Exception e) {
                    logger.error("교통 실패", e);
                    throw new RuntimeException("교통 실패: " + e.getMessage());
                }
                int hours = time / 3600;
                int minutes = (time % 3600) / 60;
                if(time / 60 > maxtraval)
                    continue;
                RentDetails rentDetails = new RentDetails();
                rentDetails.setPlace(place);
                rentDetails.setArea(area + "평");
                rentDetails.setCost((int)cost + " 만원");
                rentDetails.setTransportcost(transportcost + " 원");
                rentDetails.setTime(hours + "시간 " + minutes + "분");

                filteredTop5.add(rentDetails);

                if (filteredTop5.size() >= 5) {
                    break; // 상위 5개만 필터링하므로 5개 이상이면 종료
                }
            } catch (Exception e) {
                logger.error("카카오 API 연결 실패", e);
                throw new RuntimeException("카카오 API 연결 실패: " + e.getMessage());
            }
        }

        return filteredTop5;
    }
    @Getter
    @Setter
    class RentDetails {
        private String place;
        private String area;
        private String cost;
        private String time;
        private String transportcost;
    }

    private static final Map<Integer, String> lawdCodeToDistrictMap = new HashMap<>();

    static {
        lawdCodeToDistrictMap.put(11680, "강남구");
        lawdCodeToDistrictMap.put(11740,"강동구");
        lawdCodeToDistrictMap.put(11305, "강북구");
        lawdCodeToDistrictMap.put(11500, "강서구");
        lawdCodeToDistrictMap.put(11620, "관악구");
        lawdCodeToDistrictMap.put(11215, "광진구");
        lawdCodeToDistrictMap.put(11530, "구로구");
        lawdCodeToDistrictMap.put(11545, "금천구");
        lawdCodeToDistrictMap.put(11350, "노원구");
        lawdCodeToDistrictMap.put(11320, "도봉구");
        lawdCodeToDistrictMap.put(11230, "동대문구");
        lawdCodeToDistrictMap.put(11590, "동작구");
        lawdCodeToDistrictMap.put(11440, "마포구");
        lawdCodeToDistrictMap.put(11410, "서대문구");
        lawdCodeToDistrictMap.put(11650, "서초구");
        lawdCodeToDistrictMap.put(11200, "성동구");
        lawdCodeToDistrictMap.put(11290, "성북구");
        lawdCodeToDistrictMap.put(11710, "송파구");
        lawdCodeToDistrictMap.put(11470, "양천구");
        lawdCodeToDistrictMap.put(11560, "영등포구");
        lawdCodeToDistrictMap.put(11170, "용산구");
        lawdCodeToDistrictMap.put(11380, "은평구");
        lawdCodeToDistrictMap.put(11110, "종로구");
        lawdCodeToDistrictMap.put(11140, "중구");
        lawdCodeToDistrictMap.put(11260, "중랑구");
    }
}