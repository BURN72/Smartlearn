export const CourseStatus = {
  BROUILLON: 'BROUILLON',
  EN_REVISION: 'EN_REVISION',
  PUBLIE: 'PUBLIE',
  REJETE: 'REJETE',
  ARCHIVE: 'ARCHIVE'
}

export const CourseStatusLabels = {
  [CourseStatus.BROUILLON]: 'Brouillon',
  [CourseStatus.EN_REVISION]: 'En révision',
  [CourseStatus.PUBLIE]: 'Publié',
  [CourseStatus.REJETE]: 'Rejeté',
  [CourseStatus.ARCHIVE]: 'Archivé'
}

export const CourseStatusColors = {
  [CourseStatus.BROUILLON]: 'bg-gray-100 text-gray-600',
  [CourseStatus.EN_REVISION]: 'bg-yellow-100 text-yellow-700',
  [CourseStatus.PUBLIE]: 'bg-green-100 text-green-700',
  [CourseStatus.REJETE]: 'bg-red-100 text-red-600',
  [CourseStatus.ARCHIVE]: 'bg-gray-100 text-gray-500'
}