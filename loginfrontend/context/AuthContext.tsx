"use client";

import React, { createContext, useContext, useEffect, useState } from "react";
import { User, getUserProfile } from "@/lib/api";

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (token: string, user: User) => void;
  logout: () => void;
  refreshProfile: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // Initialize from localStorage on browser mount
  useEffect(() => {
    async function initAuth() {
      if (typeof window === "undefined") return;

      const storedToken = localStorage.getItem("auth_token");
      const storedUser = localStorage.getItem("auth_user");

      if (storedToken) {
        setToken(storedToken);
        if (storedUser) {
          try {
            setUser(JSON.parse(storedUser));
          } catch {
            // ignore JSON parse error
          }
        }

        // Verify token with backend
        try {
          const freshUser = await getUserProfile(storedToken);
          setUser(freshUser);
          localStorage.setItem("auth_user", JSON.stringify(freshUser));
        } catch {
          // Token is expired or invalid
          console.warn("Stored JWT token expired or invalid, logging out.");
          localStorage.removeItem("auth_token");
          localStorage.removeItem("auth_user");
          setToken(null);
          setUser(null);
        }
      }

      setLoading(false);
    }

    initAuth();
  }, []);

  const login = (newToken: string, newUser: User) => {
    setToken(newToken);
    setUser(newUser);
    if (typeof window !== "undefined") {
      localStorage.setItem("auth_token", newToken);
      localStorage.setItem("auth_user", JSON.stringify(newUser));
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    if (typeof window !== "undefined") {
      localStorage.removeItem("auth_token");
      localStorage.removeItem("auth_user");
    }
  };

  const refreshProfile = async () => {
    if (!token) return;
    try {
      const freshUser = await getUserProfile(token);
      setUser(freshUser);
      localStorage.setItem("auth_user", JSON.stringify(freshUser));
    } catch (err) {
      console.error("Failed to refresh profile:", err);
      logout();
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        logout,
        refreshProfile,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
