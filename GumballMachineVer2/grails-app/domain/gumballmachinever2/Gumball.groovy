package gumballmachinever2

class Gumball {

    String modelNumber
    String serialNumber
    Integer countGumballs

    static constraints = {
        serialNumber(unique: true)
    }
}
