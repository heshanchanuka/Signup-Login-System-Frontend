"use client";

import React, { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";

export default function DashboardPage() {
  const router = useRouter();
  const { user, token, loading, logout } = useAuth();

  // Protected route guard
  useEffect(() => {
    if (!loading && !token) {
      router.push("/login");
    }
  }, [loading, token, router]);

  if (loading || (!token && typeof window !== "undefined")) {
    return (
      <div className="flex-1 flex items-center justify-center min-h-[60vh] px-4">
        <div className="flex flex-col items-center gap-3">
          <div className="w-8 h-8 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
          <p className="text-xs text-zinc-500 font-medium">Verifying session...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 max-w-4xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-10">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-indigo-900 via-indigo-800 to-zinc-900 text-white rounded-2xl sm:rounded-3xl p-6 sm:p-8 md:p-10 shadow-xl shadow-indigo-950/20 mb-6 sm:mb-8 relative overflow-hidden">
        <div className="absolute right-0 top-0 translate-x-8 -translate-y-8 w-48 sm:w-64 h-48 sm:h-64 bg-indigo-500/20 rounded-full blur-3xl pointer-events-none" />

        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-5 sm:gap-6 relative z-10">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-medium bg-white/10 backdrop-blur-md mb-2.5 sm:mb-3 text-indigo-200">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              Authenticated Session
            </div>
            <h1 className="text-xl sm:text-2xl md:text-3xl font-extrabold tracking-tight">
              Hello, {user?.userFirstName || user?.userName}! 👋
            </h1>
            <p className="text-xs sm:text-sm text-indigo-200 mt-1 max-w-xl">
              Welcome to your personal dashboard.
            </p>
          </div>

          <button
            onClick={() => {
              logout();
              router.push("/login");
            }}
            className="w-full sm:w-auto min-h-[44px] px-4 py-2.5 rounded-xl font-medium text-xs sm:text-sm bg-white/10 hover:bg-white/20 active:bg-white/30 text-white border border-white/20 backdrop-blur-md transition-colors text-center"
          >
            Sign Out
          </button>
        </div>
      </div>

      {/* User Profile Card */}
      <div className="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl sm:rounded-3xl p-6 sm:p-8 shadow-sm">
        <div className="flex flex-col sm:flex-row items-center sm:items-start text-center sm:text-left gap-4 sm:gap-6 mb-6 sm:mb-8">
          <div className="w-16 h-16 sm:w-20 sm:h-20 rounded-2xl bg-gradient-to-tr from-indigo-600 to-cyan-400 text-white flex items-center justify-center text-2xl sm:text-3xl font-bold shadow-md shadow-indigo-500/20 shrink-0">
            {user?.userFirstName ? user.userFirstName.charAt(0).toUpperCase() : "U"}
          </div>
          <div>
            <h2 className="font-bold text-lg sm:text-xl text-zinc-900 dark:text-white">
              {user?.userFirstName} {user?.userLastName}
            </h2>
            <p className="text-xs sm:text-sm text-zinc-500 dark:text-zinc-400">@{user?.userName}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4 pt-5 sm:pt-6 border-t border-zinc-100 dark:border-zinc-800 text-sm">
          <div className="p-3.5 sm:p-4 rounded-xl sm:rounded-2xl bg-zinc-50 dark:bg-zinc-800/50 border border-zinc-100 dark:border-zinc-800">
            <span className="block text-[11px] sm:text-xs text-zinc-400 mb-0.5 sm:mb-1">Username</span>
            <span className="font-semibold text-zinc-800 dark:text-zinc-200 text-sm sm:text-base break-all">
              {user?.userName}
            </span>
          </div>

          <div className="p-3.5 sm:p-4 rounded-xl sm:rounded-2xl bg-zinc-50 dark:bg-zinc-800/50 border border-zinc-100 dark:border-zinc-800">
            <span className="block text-[11px] sm:text-xs text-zinc-400 mb-0.5 sm:mb-1">First Name</span>
            <span className="font-semibold text-zinc-800 dark:text-zinc-200 text-sm sm:text-base break-all">
              {user?.userFirstName}
            </span>
          </div>

          <div className="p-3.5 sm:p-4 rounded-xl sm:rounded-2xl bg-zinc-50 dark:bg-zinc-800/50 border border-zinc-100 dark:border-zinc-800">
            <span className="block text-[11px] sm:text-xs text-zinc-400 mb-0.5 sm:mb-1">Last Name</span>
            <span className="font-semibold text-zinc-800 dark:text-zinc-200 text-sm sm:text-base break-all">
              {user?.userLastName}
            </span>
          </div>

          <div className="p-3.5 sm:p-4 rounded-xl sm:rounded-2xl bg-zinc-50 dark:bg-zinc-800/50 border border-zinc-100 dark:border-zinc-800">
            <span className="block text-[11px] sm:text-xs text-zinc-400 mb-0.5 sm:mb-1">Account Status</span>
            <span className="inline-flex items-center gap-1.5 font-semibold text-emerald-600 dark:text-emerald-400 text-sm sm:text-base">
              <span className="w-2 h-2 rounded-full bg-emerald-500" />
              Active
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
