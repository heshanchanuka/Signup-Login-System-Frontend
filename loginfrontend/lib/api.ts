export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8083";

export interface User {
  userName: string;
  userFirstName: string;
  userLastName: string;
}

export interface SignupPayload {
  userName: string;
  userFirstName: string;
  userLastName: string;
  userPassword: string;
}

export interface LoginPayload {
  username: string;
  password: string;
}

export interface LoginResponse {
  user: User;
  jwtToken: string;
}

export interface ApiError {
  timestamp?: string;
  status?: number;
  error?: string;
  message: string;
}

/**
 * Handle API responses and extract meaningful error messages.
 */
async function handleResponse<T>(res: Response): Promise<T> {
  const contentType = res.headers.get("content-type") || "";
  const isJson = contentType.includes("application/json");
  const data = isJson ? await res.json() : await res.text();

  if (!res.ok) {
    let errorMessage = "An unexpected error occurred";
    if (typeof data === "object" && data !== null) {
      errorMessage = (data as ApiError).message || (data as ApiError).error || errorMessage;
    } else if (typeof data === "string" && data.trim().length > 0) {
      errorMessage = data;
    }
    throw new Error(errorMessage);
  }

  return data as T;
}

/**
 * Signup API Call -> POST /user/register-new-user
 */
export async function signupUser(payload: SignupPayload): Promise<User> {
  const res = await fetch(`${API_BASE_URL}/user/register-new-user`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  return handleResponse<User>(res);
}

/**
 * Login API Call -> POST /authentication
 */
export async function loginUser(payload: LoginPayload): Promise<LoginResponse> {
  const res = await fetch(`${API_BASE_URL}/authentication`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  return handleResponse<LoginResponse>(res);
}

/**
 * Fetch Current User Profile -> GET /user/profile with Bearer Token
 */
export async function getUserProfile(token: string): Promise<User> {
  const res = await fetch(`${API_BASE_URL}/user/profile`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  return handleResponse<User>(res);
}
