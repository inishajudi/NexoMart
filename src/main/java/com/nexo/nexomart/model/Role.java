package com.nexo.nexomart.model;

/**
 * The three roles a user account can hold in NexoMart.
 * ADMIN is never created through the public signup flow — it is seeded directly
 * into the database (see seed.sql), per spec F1.
 */
public enum Role {
    BUYER,
    SELLER,
    ADMIN
}
