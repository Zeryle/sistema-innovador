export interface UserDto {
  id: number;
  email: string;
  role: 'OWNER' | 'MECHANIC' | 'RECEPTIONIST';
  tenantId: string;
  active: boolean;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: UserDto;
  tenant: { id: string; businessName: string; plan: string };
}

export interface LoginCommand {
  email: string;
  password: string;
}

export interface RegisterCommand {
  email: string;
  password: string;
  businessName: string;
  phone: string;
  role?: string;
}
