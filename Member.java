package com.library.model;

import java.time.LocalDate;

/**
 * Represents a library member.
 */
public class Member {

    private int       memberId;
    private String    name;
    private String    email;
    private String    phone;
    private String    address;
    private LocalDate joinedDate;
    private boolean   isActive;

    // ── Constructors ─────────────────────────────────────────

    public Member() {}

    public Member(String name, String email, String phone, String address) {
        this.name       = name;
        this.email      = email;
        this.phone      = phone;
        this.address    = address;
        this.joinedDate = LocalDate.now();
        this.isActive   = true;
    }

    // ── Getters & Setters ────────────────────────────────────

    public int       getMemberId()           { return memberId; }
    public void      setMemberId(int id)     { this.memberId = id; }

    public String    getName()               { return name; }
    public void      setName(String n)       { this.name = n; }

    public String    getEmail()              { return email; }
    public void      setEmail(String e)      { this.email = e; }

    public String    getPhone()              { return phone; }
    public void      setPhone(String p)      { this.phone = p; }

    public String    getAddress()            { return address; }
    public void      setAddress(String a)    { this.address = a; }

    public LocalDate getJoinedDate()         { return joinedDate; }
    public void      setJoinedDate(LocalDate d){ this.joinedDate = d; }

    public boolean   isActive()              { return isActive; }
    public void      setActive(boolean b)    { this.isActive = b; }

    @Override
    public String toString() {
        return String.format(
            "[%d] %-25s | %-30s | %-15s | Active: %s",
            memberId, name, email, phone, isActive ? "Yes" : "No");
    }
}
