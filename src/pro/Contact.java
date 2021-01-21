package pro;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Stefania
 */
public  class Contact{
    private final String nume;
    private final String prenume;
    private final String numar;
    
    public Contact(String nume, String prenume,String numar){
    this.nume=new String(nume);
    this.prenume=new String(prenume);
    this.numar=new String(numar);
    
    }
     public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getNumar() {
        return numar;
    }}
