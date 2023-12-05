import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, tap } from 'rxjs';
import { Teacher } from '../../../interfaces/Teacher';
import { TeacherService } from '../../../services/teacher/teacher.service';

@Component({
  selector: 'app-teacher-edit',
  templateUrl: './teacher-edit.component.html',
  styleUrls: ['./teacher-edit.component.css']
})
export class TeacherEditComponent {

  teacher!: Teacher;
  teacherId: number = 0;

  constructor(private route: ActivatedRoute, private teacherService: TeacherService, private router: Router) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params): void => {
      this.teacherId = parseInt(<string>params.get('id'));
      this.getTeacherById(this.teacherId);
    });
  }

  getTeacherById(id: number): void {
    this.teacherService.getTeacherById(id).subscribe((teacher: Teacher): void => {
      this.teacher = teacher;
    });
  }

  updateTeacher(): void {
    this.teacher.salary = this.teacherService.formatCurrency(this.teacher.salary);
    console.log(this.teacher)
    let op: boolean = confirm('Deseja atualizar o professor?');
    if (op) {
      this.teacherService.updateTeacher(this.teacherId, this.teacher)
        .pipe(
          tap((response): void => {
          }),
          catchError(err => {
            throw err;
          }))
        .subscribe();
      this.router.navigate(['/teacher-list']);
    }
  }

  cancel(): void {
    this.router.navigate(['/teacher-list']);
  }
}
