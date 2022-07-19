package zyrosite.pocketexam.ui;

import zyrosite.pocketexam.SetModel;
import zyrosite.pocketexam.StudyMaterialModel;

class HomePageModel {
    private int type;
    private SetModel setModel;
    private StudyMaterialModel studyModel;

    public HomePageModel() {
    }

    public HomePageModel(int type, SetModel setModel) {
        this.type = type;
        this.setModel = setModel;
    }

    public HomePageModel(int type, StudyMaterialModel object) {
        this.type = type;
        this.studyModel = object;
    }

    public int getType() {
        return type;
    }

    public SetModel getSetModel() {
        return setModel;
    }

    public StudyMaterialModel getStudyModel() {
        return studyModel;
    }
}
