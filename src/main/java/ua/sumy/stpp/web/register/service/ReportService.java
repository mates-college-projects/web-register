package ua.sumy.stpp.web.register.service;

import ua.sumy.stpp.web.register.model.Mark;

import java.util.List;

public class ReportService {

    public Mark calculateAverageMark(List<Mark> marks) {
        final int markCount = marks.size();
        double marksSum = 0;
        for (Mark mark : marks) {
            marksSum += mark.getValue();
        }
        return new Mark(marksSum / markCount);
    }

    public Mark calculateAbsoluteMark(List<Mark> marks) {
        final int markCount = marks.size();
        int goodMarksNumber = 0;
        for (Mark mark : marks) {
            if (mark.getValue() > 3) {
                goodMarksNumber++;
            }
        }
        return new Mark(((double) goodMarksNumber * 100) / markCount);
    }

    public Mark calculateQualitativeMark(List<Mark> marks) {
        final int markCount = marks.size();
        int positiveMarksNumber = 0;
        for (Mark mark : marks) {
            if (mark.getValue() > 2) {
                positiveMarksNumber++;
            }
        }
        return new Mark(((double) positiveMarksNumber * 100) / markCount);
    }
}
