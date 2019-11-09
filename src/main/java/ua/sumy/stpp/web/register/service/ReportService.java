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
        double goodMarksSum = 0;
        for (Mark mark : marks) {
            double currentMarkValue = mark.getValue();
            if (currentMarkValue > 3) {
                goodMarksSum += currentMarkValue;
            }
        }
        return new Mark(goodMarksSum / markCount);
    }

    public Mark calculateQualitativeMark(List<Mark> marks) {
        final int markCount = marks.size();
        double positiveMarksSum = 0;
        for (Mark mark : marks) {
            double currentMarkValue = mark.getValue();
            if (currentMarkValue > 2) {
                positiveMarksSum += currentMarkValue;
            }
        }
        return new Mark(positiveMarksSum / markCount);
    }
}
