package zyrosite.pocketexam.registration;

class UserModel {
    private String ID, Email, Phone, Password, DisplayName, PhotoUrl;
    public UserModel(String ID, String email, String phone, String password, String displayName, String photoUrl) {
        this.ID = ID;
        Email = email;
        Phone = phone;
        Password = password;
        DisplayName = displayName;
        PhotoUrl = photoUrl;
    }
}
