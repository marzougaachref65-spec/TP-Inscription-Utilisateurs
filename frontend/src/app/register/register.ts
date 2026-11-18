import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { User } from '../models/user.model';
import { AuthService } from '../services/auth.service';

/** TP 2025 : verifier que les deux mots de passe saisis sont identiques. */
export function passwordsMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  return password === confirmPassword ? null : { passwordsMismatch: true };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register implements OnInit {

  public user = new User();
  confirmPassword?: string;
  myForm!: FormGroup;
  err = '';
  loading = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.myForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: passwordsMatchValidator });
  }

  onRegister(): void {
    if (this.myForm.invalid) {
      this.myForm.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }

    const { username, email, password } = this.myForm.value;
    this.user = new User();
    this.user.username = username;
    this.user.email = email;
    this.user.password = password;

    this.err = '';
    this.loading = true;
    this.cdr.markForCheck();

    this.authService.registerUser(this.user).subscribe({
      next: () => {
        this.authService.setRegistredUser(this.user);
        this.loading = false;
        this.toastr.success('Veuillez confirmer votre email', 'Confirmation');
        this.router.navigate(['/verifEmail']);
      },
      error: (err: any) => {
        this.loading = false;
        this.err = this.extractError(err);
        this.toastr.error(this.err, 'Inscription');
        this.cdr.markForCheck();
      }
    });
  }

  private extractError(err: any): string {
    const code = err?.error?.errorCode;
    if (code === 'USER_EMAIL_ALREADY_EXISTS') {
      return 'Cet email est déjà utilisé !';
    }
    if (code === 'USER_USERNAME_ALREADY_EXISTS') {
      return "Ce nom d'utilisateur est déjà utilisé !";
    }
    if (err?.error?.message) {
      return err.error.message;
    }
    return "Impossible de s'inscrire. Veuillez réessayer.";
  }
}
