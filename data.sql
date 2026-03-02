CREATE TABLE IF NOT EXISTS plants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    manfaat TEXT NOT NULL,
    efek_samping TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Migration: Create toyotas table
CREATE TABLE IF NOT EXISTS toyotas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama_mobil VARCHAR(100) NOT NULL,
    tipe_mobil VARCHAR(100) NOT NULL,
    deskripsi TEXT NOT NULL,
    harga BIGINT NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );