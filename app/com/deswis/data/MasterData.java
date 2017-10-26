package com.deswis.data;

import com.deswis.utils.Config;
import models.Category;
import models.Destination;
import models.GooglePlace;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MasterData {

    private static MasterData instance = null;
    private static File file = new File(Config.getInstance().getPath() + "conf//data//data.xlsx");

    public static MasterData getInstance() {
        if (instance == null) {
            instance = new MasterData();
        }
        return instance;
    }

    @SuppressWarnings("unused")
    public void cleanAll() {
        List<Destination> destinations = Destination.find.all();
        for (Destination destination : destinations) destination.delete();
    }

    public void createDestination() {
        if (Destination.find.all().size() == 0) {
            try {
                if (file != null) {
                    FileInputStream fis = new FileInputStream(file);
                    XSSFWorkbook workbook = new XSSFWorkbook(fis);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 1) {
                            Iterator<Cell> cellIterator = row.cellIterator();
                            Destination deswis = new Destination();
                            while (cellIterator.hasNext()) {
                                Cell cell = cellIterator.next();
                                switch (cell.getColumnIndex()) {
                                    case 1:
                                        deswis.name = cell.getStringCellValue();
                                        break;
                                    case 2:
                                        deswis.phone = cell.getStringCellValue();
                                        break;
                                    case 3:
                                        deswis.rating = cell.getNumericCellValue();
                                        break;
                                    case 4:
                                        deswis.lengthOfVisit = cell.getNumericCellValue();
                                        break;
                                    case 5:
                                        deswis.categories = cell.getStringCellValue();
                                        break;
                                    case 6:
                                        deswis.description = cell.getStringCellValue();
                                        break;
                                    case 7:
                                        deswis.urlSource = cell.getStringCellValue();
                                        break;
                                    case 8:
                                        deswis.tariff = cell.getNumericCellValue();
                                        break;
                                }
                                deswis.createdAt = new Date();
                                deswis.updatedAt = new Date();
                                deswis.save();
                            }
                        }
                    }
                    workbook.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void createCategory() {
        if (Category.find.all().size() == 0) {
            try {
                if (file != null) {
                    FileInputStream fis = new FileInputStream(file);
                    XSSFWorkbook workbook = new XSSFWorkbook(fis);
                    XSSFSheet sheet = workbook.getSheetAt(1);
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 1) {
                            Iterator<Cell> cellIterator = row.cellIterator();
                            Category category = new Category();
                            while (cellIterator.hasNext()) {
                                Cell cell = cellIterator.next();
                                switch (cell.getColumnIndex()) {
                                    case 0:
                                        category.name = cell.getStringCellValue();
                                        break;
                                    case 1:
                                        category.children = cell.getStringCellValue();
                                        break;
                                    case 2:
                                        category.hint = cell.getStringCellValue();
                                        break;
                                }
                                category.createdAt = new Date();
                                category.updatedAt = new Date();
                                category.save();
                            }
                        }
                    }
                    workbook.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createGooglePlace() {
        if (GooglePlace.find.all().size() == 0) {
            try {
                if (file != null) {
                    FileInputStream fis = new FileInputStream(file);
                    XSSFWorkbook workbook = new XSSFWorkbook(fis);
                    XSSFSheet sheet = workbook.getSheetAt(2);
                    for (Row row : sheet) {
                        if (row.getRowNum() >= 1) {
                            Iterator<Cell> cellIterator = row.cellIterator();
                            GooglePlace googlePlace = new GooglePlace();
                            while (cellIterator.hasNext()) {
                                Cell cell = cellIterator.next();
                                switch (cell.getColumnIndex()) {
                                    case 1:
                                        googlePlace.lat = cell.getNumericCellValue();
                                        break;
                                    case 2:
                                        googlePlace.lng = cell.getNumericCellValue();
                                        break;
                                    case 3:
                                        googlePlace.name = cell.getStringCellValue();
                                }
                                googlePlace.createdAt = new Date();
                                googlePlace.updatedAt = new Date();
                                googlePlace.save();
                            }
                        }
                    }
                    workbook.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
